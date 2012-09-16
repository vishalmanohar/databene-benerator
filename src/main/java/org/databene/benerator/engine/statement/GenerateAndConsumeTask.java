/*
 * (c) Copyright 2007-2012 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.databene.benerator.engine.statement;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.databene.benerator.Consumer;
import org.databene.benerator.composite.ComponentBuilder;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.BeneratorMonitor;
import org.databene.benerator.engine.CurrentProductGeneration;
import org.databene.benerator.engine.LifeCycleHolder;
import org.databene.benerator.engine.ScopedLifeCycleHolder;
import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.ResourceManagerSupport;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.StatementUtil;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.IOUtil;
import org.databene.commons.MessageHolder;
import org.databene.commons.Resettable;
import org.databene.script.Expression;
import org.databene.script.expression.ExpressionUtil;
import org.databene.task.PageListener;
import org.databene.task.Task;
import org.databene.task.TaskResult;

/**
 * Task that creates one data set instance per run() invocation and sends it to the specified consumer.<br/><br/>
 * Created: 01.02.2008 14:39:11
 * @author Volker Bergmann
 */
public class GenerateAndConsumeTask implements Task, PageListener, ResourceManager, MessageHolder {

	private String taskName;
    private BeneratorContext context;
    private ResourceManager resourceManager;
    
    private List<Statement> statements;
    private List<LifeCycleHolder> scopeds;
    private Expression<Consumer> consumerExpr;

    private volatile AtomicBoolean initialized;
    private Consumer consumer;
    private String message;
	private String productName;
    
    public GenerateAndConsumeTask(String taskName, String productName) {
    	this.taskName = taskName;
    	this.productName = productName;
        this.resourceManager = new ResourceManagerSupport();
        this.initialized = new AtomicBoolean(false);
    	this.statements = new ArrayList<Statement>();
    	this.scopeds = new ArrayList<LifeCycleHolder>();
    }

    // interface -------------------------------------------------------------------------------------------------------

    public void addStatement(Statement statement) {
    	this.statements.add(statement);
    }
    
    public void setStatements(List<Statement> statements) {
    	this.statements.clear();
    	for (Statement statement : statements)
    		this.addStatement(statement);
    }
    
    public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setConsumer(Expression<Consumer> consumerExpr) {
        this.consumerExpr = consumerExpr;
	}
    
    public Consumer getConsumer() {
    	return consumer;
    }
	
	public void init(BeneratorContext context) {
	    synchronized (initialized) {
	    	if (!initialized.get()) {
	    		this.context = context;
	    		this.consumer = ExpressionUtil.evaluate(consumerExpr, context);
    			resourceManager.addResource(consumer);
	    		injectConsumptionStart();
	    		injectConsumptionEnd();
	    		initialized.set(true);
	        	initStatements(context);
	        	checkScopes(statements);
	    	}
	    }
    }

	public String getProductName() {
		return productName;
	}

	public ProductWrapper<?> getRecentProduct() {
		return context.getCurrentProduct();
	}

    // Task interface implementation -----------------------------------------------------------------------------------
    
	public String getTaskName() {
	    return taskName;
    }

    public boolean isThreadSafe() {
        return false;
    }
    
    public boolean isParallelizable() {
        return false;
    }
    
    public TaskResult execute(Context ctx, ErrorHandler errorHandler) {
    	message = null;
    	if (!initialized.get())
    		init((BeneratorContext) ctx);
    	try {
    		boolean success = true;
        	for (int i = 0; i < statements.size(); i++) {
        		Statement statement = statements.get(i);
				success &= statement.execute(context);
				if (!success && (statement instanceof ValidationStatement)) {
					i = -1; // if the product is not valid, restart with the first statement
					success = true;
					continue;
				}
				if (!success) {
					if (statement instanceof MessageHolder)
						this.message = ((MessageHolder) statement).getMessage();
					break;
				}
			}
        	if (success)
        		BeneratorMonitor.INSTANCE.countGenerations(1);
	        Thread.yield();
	        return (success ? TaskResult.EXECUTING : TaskResult.UNAVAILABLE);
    	} catch (Exception e) {
			errorHandler.handleError("Error in execution of task " + getTaskName(), e);
    		return TaskResult.EXECUTING; // stay available if the ErrorHandler has not canceled execution
    	}
    }
    
    public void reset() {
    	enqueueResets(statements);
        performLocalResets();
    }

	public void close() {
        // close sub statements
        for (Statement statement : statements) {
			statement = StatementUtil.getRealStatement(statement, context);
		    if (statement instanceof Closeable)
		    	IOUtil.close((Closeable) statement);
		}
        // close resource manager
        resourceManager.close();
    }
    
    
    // PageListener interface ------------------------------------------------------------------------------------------
    
	public void pageStarting() {
		// nothing special to do on page start
	}
    
    public void pageFinished() {
    	IOUtil.flush(consumer);
    }
    

    // ResourceManager interface ---------------------------------------------------------------------------------------
    
	public boolean addResource(Closeable resource) {
	    return resourceManager.addResource(resource);
    }
	
	// MessageHolder interface -----------------------------------------------------------------------------------------
	
	public String getMessage() {
	    return message;
    }
	
	// java.lang.Object overrides --------------------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + taskName + ')';
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private void injectConsumptionStart() {
		// find last sub member generation...
		int lastMemberIndex = - 1;
		for (int i = statements.size() - 1; i >= 0; i--) {
			Statement statement = statements.get(i);
			if (statement instanceof ComponentBuilder || statement instanceof CurrentProductGeneration 
					|| statement instanceof ValidationStatement || statement instanceof ConversionStatement) {
				lastMemberIndex = i;
				break;
			}
		}
		// ...and insert consumption start statement immediately after that one
		ConsumptionStatement consumption = new ConsumptionStatement(consumer, true, false);
		statements.add(lastMemberIndex + 1, consumption);
	}

	private void injectConsumptionEnd() {
		// find last sub generation statement...
		int lastSubGenIndex = statements.size() - 1;
		for (int i = statements.size() - 1; i >= 0; i--) {
			Statement statement = statements.get(i);
			if (statement instanceof GenerateOrIterateStatement) {
				lastSubGenIndex = i;
				break;
			}
		}
		// ...and insert consumption finish statement immediately after that one
		ConsumptionStatement consumption = new ConsumptionStatement(consumer, false, true);
		statements.add(lastSubGenIndex + 1, consumption);
	}

	public void initStatements(BeneratorContext context) {
		for (Statement statement : statements) {
			statement = StatementUtil.getRealStatement(statement, context);
			if (statement instanceof LifeCycleHolder)
			    ((LifeCycleHolder) statement).init(context);
		}
	}

    private void checkScopes(List<Statement> statements) {
        for (Statement statement : statements) {
			statement = StatementUtil.getRealStatement(statement, context);
		    if (statement instanceof ScopedLifeCycleHolder) {
		    	ScopedLifeCycleHolder holder = (ScopedLifeCycleHolder) statement;
		    	String scope = holder.getScope();
		    	if (scope == null || productName.equals(scope))
		    		scopeds.add(holder);
		    } else if (statement instanceof GenerateOrIterateStatement) {
		    	GenerateOrIterateStatement subGenerate = (GenerateOrIterateStatement) statement;
		    	checkScopes(subGenerate.getTask().statements);
		    }
		}
    }

    private void enqueueResets(List<Statement> statements) {
    	for (LifeCycleHolder scoped : scopeds)
    		scoped.reset();
    }

	private void performLocalResets() {
		for (Statement statement : statements) {
			statement = StatementUtil.getRealStatement(statement, context);
		    if (statement instanceof ScopedLifeCycleHolder) {
		    	ScopedLifeCycleHolder holder = (ScopedLifeCycleHolder) statement;
				holder.resetIfNeeded();
		    } else if (statement instanceof Resettable) {
		    	((Resettable) statement).reset();
		    }
		}
	}
    
}

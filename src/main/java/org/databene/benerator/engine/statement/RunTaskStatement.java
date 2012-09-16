/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
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

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.Statement;
import org.databene.commons.ErrorHandler;
import org.databene.script.Expression;
import org.databene.task.PageListener;
import org.databene.task.Task;
import org.databene.task.runner.PagedTaskRunner;

/**
 * {@link Statement} that executes a {@link Task} supporting paging and multithreading.<br/><br/>
 * Created: 27.10.2009 20:29:47
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class RunTaskStatement extends AbstractStatement implements Closeable {
	
	protected Expression<? extends Task> taskProvider;
	protected Task task;
	protected Expression<Long> count;
	protected Expression<Long> pageSize;
	protected Expression<Integer> threads;
	protected Expression<PageListener> pageListener;
	protected Expression<Boolean> stats;
	protected Expression<ErrorHandler> errorHandler;
	protected boolean infoLog;

	public RunTaskStatement(Expression<? extends Task> taskProvider, 
			Expression<Long> count, Expression<Long> pageSize, 
			Expression<PageListener> pageListener, Expression<Integer> threads, 
			Expression<Boolean> stats, Expression<ErrorHandler> errorHandler,
			boolean infoLog) {
		super(errorHandler);
	    this.taskProvider = taskProvider;
	    this.count = count;
	    this.pageSize = pageSize;
	    this.threads = threads;
	    this.pageListener = pageListener;
	    this.stats = stats;
	    this.errorHandler = errorHandler;
	    this.infoLog = infoLog;
    }

	public Expression<Long> getCount() {
    	return count;
    }

	public Expression<Long> getPageSize() {
    	return pageSize;
    }

	public Expression<Integer> getThreads() {
    	return threads;
    }

	public Expression<PageListener> getPager() {
    	return pageListener;
    }

	public boolean execute(BeneratorContext context) {
	    Long invocations = count.evaluate(context);
		PagedTaskRunner.execute(
	    		getTask(context), context, 
	    		invocations,
	    		invocations,
	    		getPageListeners(context), 
	    		pageSize.evaluate(context), 
	    		threads.evaluate(context),
	    		stats.evaluate(context),
	    		context.getExecutorService(),
	    		getErrorHandler(context),
	    		infoLog);
    	return true;
	}

	public synchronized Task getTask(BeneratorContext context) {
		if (task == null)
			task = taskProvider.evaluate(context);
	    return task;
    }

	public void close() {
		task.close();
	}

	private List<PageListener> getPageListeners(BeneratorContext context) {
		List<PageListener> listeners = new ArrayList<PageListener>();
	    if (pageListener != null)
	    	listeners.add(pageListener.evaluate(context));
	    return listeners;
    }

}

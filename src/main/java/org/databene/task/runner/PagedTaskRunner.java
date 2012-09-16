/*
 * (c) Copyright 2007-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.task.runner;

import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.context.DefaultContext;
import org.databene.contiperf.PerformanceTracker;
import org.databene.platform.contiperf.PerfTrackingTaskProxy;
import org.databene.script.Expression;
import org.databene.script.expression.ExpressionUtil;
import org.databene.task.PageListener;
import org.databene.task.StateTrackingTaskProxy;
import org.databene.task.Task;
import org.databene.task.TaskUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * {@link TaskRunner} implementation that provides for repeated, paged, 
 * multi- or single-threaded Task execution.<br/><br/>
 * Created: 16.07.2007 19:25:30
 * @author Volker Bergmann
 */
public class PagedTaskRunner extends AbstractTaskRunner implements Thread.UncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(PagedTaskRunner.class);

    private List<PageListener> pageListeners;

    private long pageSize;
    private int threadCount;
    
    private Expression<ExecutorService> executor;
    private PerformanceTracker tracker;

    private Throwable exception;

    // constructors ----------------------------------------------------------------------------------------------------

    public PagedTaskRunner(Task target, List<PageListener> listeners, long pageSize, int threads, 
    		boolean stats, ExecutorService executor) {
    	this(target, listeners, pageSize, threads, stats, ExpressionUtil.constant(executor), 
    			new DefaultContext(), ErrorHandler.getDefault());
    }

    public PagedTaskRunner(Task target, List<PageListener> pageListeners, long pageSize, int threads, 
    		boolean stats, Expression<ExecutorService> executor, Context context, ErrorHandler errorHandler) {
    	super(null, context, errorHandler); // call super constructor without target...
    	if (stats) {
       		target = new PerfTrackingTaskProxy(target);
        	this.tracker = ((PerfTrackingTaskProxy) target).getTracker();
    	}
    	this.target = new StateTrackingTaskProxy<Task>(target); // ...but provide it afterwards
        this.pageListeners = pageListeners;
        this.pageSize = pageSize;
        this.threadCount = threads;
        this.executor = executor;
    }

    // Task implementation ---------------------------------------------------------------------------------------------

	public long getPageSize() {
        return pageSize;
    }

    public int getThreadCount() {
        return threadCount;
    }
    
	public long run(Long requestedInvocations, Long minInvocations) {
		// first run without verification
		long countValue = run(requestedInvocations);
		// afterwards verify execution count
		if (minInvocations != null && countValue < minInvocations)
			throw new TaskUnavailableException(target, minInvocations, countValue);
		if (tracker != null)
			tracker.getCounter().printSummary(new PrintWriter(System.out), 90, 95);
		return countValue;
    }

	public long run(Long invocationCount) {
    	if (invocationCount != null && invocationCount == 0)
    		return 0;
        long queuedInvocations = 0;
        long queuedPages = 0;
        long actualCount = 0;
    	if (invocationCount != null) {
    		if (pageSize > 0)
    			queuedPages = (invocationCount + pageSize - 1) / pageSize;
	    	queuedInvocations = invocationCount;
    	}
        this.exception = null;
        if (logger.isDebugEnabled())
            logger.debug("Running PagedTask[" + getTaskName() + "]");
        int currentPageNo = 0;
        TaskRunner pageRunner;
        if (threadCount == 1)
        	pageRunner = new SingleThreadedTaskRunner(target, (pageSize > 0), context, errorHandler);
        else
        	pageRunner = new MultiThreadedTaskRunner(target, threadCount, context, executor.evaluate(context), 
        			errorHandler, tracker); // TODO v1.0 avoid pageFinished when pageSize == 0
        do {
        	try {
        		if (pageSize > 0)
        			pageStarting(currentPageNo);
	            long currentPageSize;
	            if (pageSize > 0)
	            	currentPageSize = (invocationCount == null ? pageSize : Math.min(pageSize, queuedInvocations));
	            else
	            	currentPageSize = (invocationCount == null ? 1 : Math.min(invocationCount, queuedInvocations));
	            queuedInvocations -= currentPageSize;
	            long localCount = pageRunner.run(currentPageSize);
	            actualCount += localCount;
	            if (invocationCount != null)
	            	queuedPages--;
        		if (pageSize > 0)
        			pageFinished(currentPageNo, context);
	            if (exception != null)
	                throw new RuntimeException(exception);
	            currentPageNo++;
        	} catch (Exception e) {
        		errorHandler.handleError("Error in execution of task " + getTaskName(), e);
        	}
        } while (workPending(invocationCount, queuedPages));
        if (logger.isDebugEnabled())
            logger.debug("PagedTask " + getTaskName() + " finished");
        return actualCount;
	}
	
	public String getTaskName() {
    	return target.getTaskName();
    }
    
    public static PerformanceTracker execute(Task task, Context context, Long requestedInvocations, Long minInvocations,
            List<PageListener> pageListeners, long pageSize, int threadCount, boolean stats,
            ExecutorService executorService, ErrorHandler errorHandler, boolean infoLog) {
		logExecutionInfo(task, requestedInvocations, minInvocations, pageSize, threadCount, infoLog);
		PagedTaskRunner pagedTask = new PagedTaskRunner(task, pageListeners, 
				pageSize, threadCount, stats, ExpressionUtil.constant(executorService), context, errorHandler);
		pagedTask.run(requestedInvocations, minInvocations);
		return pagedTask.tracker;
	}

    // non-public helpers ----------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
	protected boolean workPending(Long maxInvocationCount, long queuedPages) {
        if (!((StateTrackingTaskProxy<? extends Task>) target).isAvailable())
            return false;
        if (maxInvocationCount == null)
        	return true;
        return (queuedPages > 0);
	}

    protected void pageStarting(int currentPageNo) {
        if (logger.isDebugEnabled())
            logger.debug("Starting page " + (currentPageNo + 1) + " of " + getTaskName() + " with pageSize=" + pageSize);
        if (pageListeners != null)
        	for (PageListener listener : pageListeners)
        		listener.pageStarting();
    }

    protected void pageFinished(int currentPageNo, Context context) {
        if (logger.isDebugEnabled())
            logger.debug("Page " + (currentPageNo + 1) + " of " + getTaskName() + " finished");
        if (pageListeners != null)
        	for (PageListener listener : pageListeners)
        		listener.pageFinished();
    }

	private static void logExecutionInfo(Task task, Long minInvocations, Long maxInvocations, long pageSize, int threadCount, 
			boolean infoLog) {
	    if (infoLog) {
			if (logger.isInfoEnabled()) {
				String invocationInfo = executionInfo(task, minInvocations, maxInvocations, pageSize, threadCount);
				logger.info(invocationInfo);
			}
		} else if (logger.isDebugEnabled()) {
			String invocationInfo = executionInfo(task, minInvocations, maxInvocations, pageSize, threadCount);
			logger.debug(invocationInfo);
		}
    }

	private static String executionInfo(Task task, Long minInvocations, Long maxInvocations, long pageSize, int threadCount) {
	    String invocationInfo = (maxInvocations == null ? "as long as available" :
	         (maxInvocations > 1 ? maxInvocations + " times" : ""));
	    if (minInvocations != null && minInvocations > 0 && (maxInvocations == null || maxInvocations > minInvocations))
	    	invocationInfo += " requiring at least " + minInvocations + " generations";
	    if (invocationInfo.length() > 0)
	    	invocationInfo += " with page size " + pageSize + " in " 
	    		+ (threadCount > 1 ? threadCount + " threads" : "a single thread");
	    return "Running task " + task + " " + invocationInfo;
    }
	
    public void uncaughtException(Thread t, Throwable e) {
        this.exception = e;
    }

	@Override
	public String toString() {
	    return getClass().getSimpleName();
	}

}

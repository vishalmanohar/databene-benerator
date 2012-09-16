/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

package org.databene.task.runner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.contiperf.PerformanceTracker;
import org.databene.task.StateTrackingTaskProxy;
import org.databene.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link TaskRunner} implementation that is able to execute a {@link Task} with multiple threads.<br/><br/>
 * Created: 27.03.2010 14:12:16
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class MultiThreadedTaskRunner extends AbstractTaskRunner {
	
    private static final Logger logger = LoggerFactory.getLogger(MultiThreadedTaskRunner.class);

	private int threadCount;
	private ExecutorService executorService;
	private PerformanceTracker tracker;

	
	public MultiThreadedTaskRunner(Task target, int threadCount, Context context, ExecutorService executorService,
            ErrorHandler errorHandler, PerformanceTracker tracker) {
		super(target, context, errorHandler);
	    this.threadCount = threadCount;
	    this.executorService = executorService;
	    this.tracker = tracker;
    }

    public long run(Long maxInvocationCount) {
        AtomicLong counter = new AtomicLong();
        int maxLoopsPerPage = (int)((maxInvocationCount + threadCount - 1) / threadCount);
        int shorterLoops = (int)(threadCount * maxLoopsPerPage - maxInvocationCount);
        boolean threadSafe = target.isThreadSafe();
        boolean parallelizable = target.isParallelizable();
        // create threads for a page
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int threadNo = 0; threadNo < threadCount; threadNo++) {
            int loopSize = maxLoopsPerPage;
            if (maxInvocationCount >= 0 && threadNo >= threadCount - shorterLoops)
                loopSize--;
            if (loopSize > 0) {
                Task task = target;
				if (threadCount > 1 && !threadSafe) {
                    if (parallelizable) {
                   		task = BeanUtil.clone(task);
                    } else
                        throw new ConfigurationError("Since the task is not marked as thread-safe," +
                                "it must either be used in a single thread or be parallelizable.");
                }
				if (tracker != null)
					task = ((StateTrackingTaskProxy<?>) task).clone();
                TaskRunnable runner = new TaskRunnable(loopSize, counter, latch, !threadSafe);
				executorService.execute(runner);
            } else
                latch.countDown();
        }
        
        if (logger.isDebugEnabled())
            logger.debug("Waiting for end of page on " + target.getTaskName() + "...");
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (threadSafe) // TODO v1.0 call pageFinished only if it was actually run in shared mode
            target.pageFinished();
        return counter.get();
    }

	public class TaskRunnable implements Runnable {

	    private CountDownLatch latch;
		private boolean page;
		private long requestedInvocationCount;
		private AtomicLong counter;
		
	    public TaskRunnable(long requestedInvocationCount, AtomicLong counter, CountDownLatch latch, boolean page) {
	        this.latch = latch;
	        this.page = page;
	        this.requestedInvocationCount = requestedInvocationCount;
	        this.counter = counter;
	    }

	    public void run() {
	        try {
	            long count = SingleThreadedTaskRunner.runWithoutPage(target, requestedInvocationCount, context, errorHandler);
	            counter.addAndGet(count);
	            if (page)
	                target.pageFinished();
	        } finally {
	            if (latch != null)
	            	latch.countDown();
	        }
	    }
	    
	}

}

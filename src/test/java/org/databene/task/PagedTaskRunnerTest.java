/*
 * (c) Copyright 2007-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.script.Expression;
import org.databene.script.expression.ExpressionUtil;
import org.databene.task.runner.PagedTaskRunner;

import org.junit.Before;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link PagedTaskRunner}<br/><br/>
 * Created: 16.07.2007 20:02:03
 * @since 0.2
 * @author Volker Bergmann
 */
public class PagedTaskRunnerTest {
	
	BeneratorContext context;
	ErrorHandler errHandler = ErrorHandler.getDefault();
	Expression<ExecutorService> executors;

    @Before	
	public void setUp() {
	    context = new DefaultBeneratorContext();
	    executors = ExpressionUtil.constant(Executors.newSingleThreadExecutor());
    }

	@Test
    public void testSingleInvocation() throws Exception {
        checkRun(1,  1,  1,   1, 1, 1);
        checkRun(1, 10,  1,   1, 1, 1);
        checkRun(1,  1, 10,   1, 1, 1);
        checkRun(1, 10, 10,   1, 1, 1);
    }

	@Test
    public void testSingleThreadedInvocation() throws Exception {
        checkRun(10,  1,  1,   10, 10, 10);
        checkRun(10, 10,  1,    1, 10,  1);
        checkRun( 4,  3,  1,    2,  4,  2);
    }

	@Test
    public void testMultiThreadedInvocation() throws Exception {
        checkRun(10,  1, 10,   10, 10, 10);
        checkRun(10, 10, 10,    1, 10,  1);
    }

	@Test
    public void testMultiPagedInvocation() throws Exception {
        checkRun(10,  5, 1,    2, 10,  2);
        checkRun(10,  5, 2,    2, 10,  2);
        checkRun(20,  5, 2,    4, 20,  4);
    }

	@Test
    public void testNonThreadSafeTask() throws Exception {
        checkNonThreadSafeTask(1,   1, 1, 1); // single threaded
        checkNonThreadSafeTask(10, 10, 1, 1); // single threaded, single-paged
        checkNonThreadSafeTask(10,  5, 1, 1); // single threaded, multi-paged

        checkNonThreadSafeTask(10, 10, 2, 3); // multi-threaded, single-paged
        checkNonThreadSafeTask(10,  5, 2, 5); // multi-threaded, multi-paged
    }
	
	@Test(expected = TaskUnavailableException.class)
	public void testMinCount() {
        CountTask countTask = new CountTask(1);
        PagedTaskRunner pagedTask = new PagedTaskRunner(countTask, null, 2, 2, false, executors, context, errHandler);
        pagedTask.run(2L, 2L);
	}

	// helpers ---------------------------------------------------------------------------------------------------------
	
    private void checkNonThreadSafeTask(long totalInvocations, int pageSize, int threads, 
    		int expectedInstanceCount) {
        ParallelizableCounterTask.instanceCount.set(0);
        ParallelizableCounterTask task = new ParallelizableCounterTask() {
			public TaskResult execute(Context context, ErrorHandler errorHandler) { 
				return TaskResult.EXECUTING;
			}
        };
        PagedTaskRunner pagedTask = new PagedTaskRunner(task, 
        		null, pageSize, threads, false, executors, context, errHandler);
        pagedTask.run(totalInvocations, totalInvocations);
        assertEquals("Unexpected instanceCount,", expectedInstanceCount, ParallelizableCounterTask.instanceCount.get());
    }

    private void checkRun(long totalInvocations, int pageSize, int threads,
                          int expectedInitCount, int expectedRunCount, int expectedPageCount) {
        CountTask countTask = new CountTask();
        PagedTaskRunner pagedTask = new PagedTaskRunner(countTask, null, pageSize, threads, false, executors, 
        		context, errHandler);
        pagedTask.run(totalInvocations, totalInvocations);
        assertEquals("Unexpected runCount,", expectedRunCount, countTask.runCount);
        assertEquals("Unexpected pageCount,", expectedPageCount, countTask.pageCount);
        assertEquals("Unexpected closeCount,", 0, countTask.closeCount);
    }
    
}

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

import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.task.Task;
import org.databene.task.TaskResult;

/**
 * {@link TaskRunner} implementation that executes a {@link Task} with the current thread.<br/><br/>
 * Created: 27.03.2010 14:01:29
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class SingleThreadedTaskRunner extends AbstractTaskRunner {
	
	private boolean finishPage;

	public SingleThreadedTaskRunner(Task target, boolean finishPages, Context context, ErrorHandler errorHandler) {
		super(target, context, errorHandler);
	    this.finishPage = finishPages;
    }

	public long run(Long invocationCount) {
		try {
			return runWithoutPage(target, invocationCount, context, errorHandler);
		} finally {
			if (finishPage)
				target.pageFinished();
		}
    }

	public static long runWithoutPage(Task target, Long invocationCount, Context context, ErrorHandler errorHandler) {
		long actualCount = 0;
        for (int i = 0; invocationCount == null || i < invocationCount; i++) {
            TaskResult stepResult = target.execute(context, errorHandler);
			if (stepResult != TaskResult.UNAVAILABLE)
            	actualCount++;
			if (stepResult != TaskResult.EXECUTING)
				break;
        }
        return actualCount;
	}
	
}

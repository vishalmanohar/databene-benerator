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

package org.databene.platform.contiperf;

import org.databene.commons.BeanUtil;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.contiperf.PerformanceTracker;
import org.databene.task.Task;
import org.databene.task.TaskResult;

/**
 * Proxies a {@link Task} and tracks its execution times.<br/><br/>
 * Created: 25.02.2010 09:08:48
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class PerfTrackingTaskProxy extends PerfTrackingWrapper implements Task {
	
	private Task realTask;

	public PerfTrackingTaskProxy(Task realTask) {
	    this(realTask, null);
    }

	public PerfTrackingTaskProxy(Task realTask, PerformanceTracker tracker) {
	    super(tracker);
	    this.realTask = realTask;
    }

	public TaskResult execute(Context context, ErrorHandler errorHandler) {
	    try {
	        return (TaskResult) getTracker().invoke(new Object[] { context, errorHandler });
        } catch (Exception e) {
	        throw new RuntimeException(e);
        }
	}
	
	public void pageFinished() {
	    // nothing special to do here
	}
	
	@Override
    public void close() {
	    super.close();
	    realTask.close();
	}
	
    @Override
    public Object clone() {
	    return new PerfTrackingTaskProxy(BeanUtil.clone(realTask), getTracker());
    }

	public String getTaskName() {
	    return realTask.getTaskName();
    }

	public boolean isParallelizable() {
	    return realTask.isParallelizable();
    }

	public boolean isThreadSafe() {
	    return realTask.isThreadSafe();
    }

	@Override
    protected TaskInvoker getInvoker() {
	    return new TaskInvoker(realTask);
    }

}

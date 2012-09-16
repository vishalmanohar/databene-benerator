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

/**
 * Simple abstract implementation of the Task interface.<br/>
 * <br/>
 * Created: 16.07.2007 18:55:16
 * @since 0.2
 * @author Volker Bergmann
 */
public abstract class AbstractTask implements Task {

    protected String taskName;
	private boolean threadSafe;
	private boolean parallelizable;
    
    // constructor -----------------------------------------------------------------------------------------------------

    protected AbstractTask() {
        this(null);
    }
    
    protected AbstractTask(String taskName) {
        this(taskName, false, false);
    }

    protected AbstractTask(String taskName, boolean threadSafe, boolean parallelizable) {
    	if (taskName == null)
    		taskName = getClass().getSimpleName();
        setTaskName(taskName);
        this.threadSafe = threadSafe;
        this.parallelizable = parallelizable;
    }
    
    // Task interface --------------------------------------------------------------------------------------------------

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isThreadSafe() {
        return threadSafe;
    }
    
    public boolean isParallelizable() {
        return parallelizable;
    }
    
    public void pageFinished() {
        // empty
    }
    
    public void close() {
    	// empty
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + taskName + ']';
    }

}

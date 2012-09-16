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

import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;

/**
 * Helper class for testing Task handling.<br/><br/>
 * Created: 16.07.2007 19:58:41
 * @since 0.2
 * @author Volker Bergmann
 */
public class CountTask extends AbstractTask {

    public int runCount = 0;
    public int pageCount = 0;
    public int closeCount = 0;
    public int runLimit;
    
    public CountTask() {
	    this(-1);
    }

    public CountTask(int runLimit) {
    	super("Count", true, false);
	    this.runLimit = runLimit;
    }

    // Task interface --------------------------------------------------------------------------------------------------
    
    public TaskResult execute(Context context, ErrorHandler errorHandler) {
        runCount++;
        if (runLimit < 0 || runCount < runLimit)
        	return TaskResult.EXECUTING;
        else if (runCount == runLimit)
        	return TaskResult.FINISHED;
        else
        	return TaskResult.UNAVAILABLE;
    }
    
    @Override
    public void pageFinished() {
        super.pageFinished();
        pageCount++;
    }

    @Override
    public void close() {
        closeCount++;
    }
    
    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "CountTask[runCount=" + runCount + ", closeCount=" + closeCount + ']';
    }

}

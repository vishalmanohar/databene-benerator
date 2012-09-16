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

import java.io.Closeable;

import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.ThreadAware;

/**
 * Interface for the GoF 'Command' pattern.
 * General usage is to call the executeStep() method once or several times for executing the task's work.
 * After usage, close() must be called. 
 * When implementing the Task interface, you should preferably inherit from 
 * {@link AbstractTask}, this may compensate for future interface changes.<br/>
 * <br/>
 * Created: 06.07.2007 06:30:22
 * @since 0.2
 * @author Volker Bergmann
 */
public interface Task extends ThreadAware, Closeable {

    /** @return the name of the task. */
    String getTaskName();
    
    /** 
     * Executes the task's work, possibly interacting with the context.
     */
    TaskResult execute(Context context, ErrorHandler errorHandler);
    
    void pageFinished();

    void close();
    
}

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

package org.databene.task;

import org.databene.commons.BeanUtil;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;

/**
 * Task proxy that remembers the result of the last execution step and provides it as 
 * property <code>available</code>.<br/><br/>
 * Created: 05.02.2010 10:41:55
 * @since 0.6
 * @author Volker Bergmann
 */
public class StateTrackingTaskProxy<E extends Task> extends TaskProxy<E> {

	protected TaskResult state;
	
	public StateTrackingTaskProxy(E realTask) {
	    super(realTask);
	    this.state = TaskResult.EXECUTING;
    }

	public boolean isAvailable() {
		return (state == TaskResult.EXECUTING);
	}
	
	@Override
	public synchronized TaskResult execute(Context context, ErrorHandler errorHandler) {
		if (!isAvailable())
			return TaskResult.UNAVAILABLE;
	    state = super.execute(context, errorHandler);
	    return state;
	}
	
    @Override
	public StateTrackingTaskProxy<E> clone() {
	    return new StateTrackingTaskProxy<E>(BeanUtil.clone(realTask));
	}

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + realTask.toString() + ']';
    }
    
}

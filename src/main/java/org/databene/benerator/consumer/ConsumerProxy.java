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

package org.databene.benerator.consumer;

import org.databene.benerator.Consumer;
import org.databene.benerator.wrapper.ProductWrapper;

/**
 * Parent class for {@link Consumer}s that serve as proxy to other Consumers.<br/><br/>
 * Created: 22.10.2009 16:18:07
 * @since 0.6.0
 * @author Volker Bergmann
 */
public abstract class ConsumerProxy implements Consumer {

	protected Consumer target;

	public ConsumerProxy(Consumer target) {
	    this.target = target;
    }

	public Consumer getTarget() {
		return target;
	}

	public void setTarget(Consumer target) {
    	this.target = target;
    }

	public void startConsuming(ProductWrapper<?> wrapper) {
		target.startConsuming(wrapper);
	}
	
	public void finishConsuming(ProductWrapper<?> wrapper) {
	    target.finishConsuming(wrapper);
    }

	public void flush() {
	    target.flush();
    }

	public void close() {
	    target.close();
    }

}

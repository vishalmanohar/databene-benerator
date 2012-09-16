/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.Consumer;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.contiperf.Invoker;

/**
 * {@link Consumer} implementation that calls a ContiPerf {@link PerfTrackingConsumer}.<br/><br/>
 * Created: 22.10.2009 16:17:14
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class PerfTrackingConsumer extends PerfTrackingWrapper implements Consumer {
	
	private String id;
	private Consumer target;
	
	// constructors ----------------------------------------------------------------------------------------------------

	public PerfTrackingConsumer() {
	    this(null);
    }
	
	public PerfTrackingConsumer(Consumer target) {
	    this(target, "Unnamed");
    }
	
	public PerfTrackingConsumer(Consumer target, String id) {
		this.id = id;
	    this.target = target;
    }
	
	// properties ------------------------------------------------------------------------------------------------------
	
    public void setId(String id) {
    	this.id = id;
    }

    public void setTarget(Consumer target) {
	    this.target = target;
    }
	
    // Consumer interface implementation -------------------------------------------------------------------------------
    
	public void startConsuming(ProductWrapper<?> wrapper) {
	    try {
	        getTracker().invoke(new Object[] { wrapper });
        } catch (Exception e) {
	        throw new RuntimeException(e);
        }
    }
	
	public void finishConsuming(ProductWrapper<?> wrapper) {
		target.finishConsuming(wrapper);
    }

	public void flush() {
	    target.flush();
    }

	@Override
	public void close() {
	    super.close();
	    target.close();
	}
	
	// PerfTrackingWrapper callback method implementation --------------------------------------------------------------
	
	@Override
    protected Invoker getInvoker() {
		return new ConsumerInvoker(id, target);
	}

}

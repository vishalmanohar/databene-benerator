/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
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

import java.io.Closeable;

import org.databene.contiperf.Invoker;
import org.databene.contiperf.PerformanceRequirement;
import org.databene.contiperf.PerformanceTracker;
import org.databene.contiperf.report.ConsoleReportModule;
import org.databene.contiperf.report.ReportContext;

/**
 * Common parent class for Benerator runners that support performance tracking.<br/><br/>
 * Created: 14.03.2010 10:59:00
 * @since 0.6.0
 * @author Volker Bergmann
 */
public abstract class PerfTrackingWrapper implements Closeable {
	
	private PerformanceTracker tracker;
	private PerformanceRequirement requirement;
	private ReportContext context;

	protected abstract Invoker getInvoker();

	public PerfTrackingWrapper() {
	    this(null); // allow for lazy PerfromanceTracker initialization
    }
	
	public PerfTrackingWrapper(PerformanceTracker tracker) {
	    this.tracker = tracker;
	    this.requirement = new PerformanceRequirement();
	    this.context = new BeneratorCpfReportContext();
	    context.addReportModule(new ConsoleReportModule());
    }
	
	public void setMax(int max) {
		requirement.setMax(max);
	}

	public void setPercentiles(String percentilesSpec) {
		requirement.setPercentiles(percentilesSpec);
	}
	
	public void setContext(ReportContext context) {
		this.context = context;
	}
	
	public PerformanceTracker getTracker() {
		if (tracker == null) {
			// the tracker is initialized lazily for allowing the class to be first constructed in a simple way 
			// and then be configured by calling the property setters.
			Invoker invoker = getInvoker();
			tracker = new PerformanceTracker(invoker, requirement, context);
		}
		return tracker;
	}

    public void close() {
    	if (tracker.isCounterStarted())
    		tracker.stopCounter();
    }

}

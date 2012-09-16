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

package org.databene.platform;

import static org.junit.Assert.*;

import org.databene.commons.ErrorHandler;
import org.databene.commons.context.DefaultContext;
import org.databene.platform.contiperf.PerfTrackingTaskProxy;
import org.databene.stat.LatencyCounter;
import org.databene.task.Task;
import org.databene.task.TaskMock;
import org.databene.task.TaskResult;
import org.junit.Test;

/**
 * Tests the {@link PerfTrackingTaskProxy}.<br/><br/>
 * Created: 25.02.2010 09:16:55
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class PerfTrackingTaskProxyTest {

	@Test
	public void test() throws Exception {
		DefaultContext context = new DefaultContext();
		Task task = new TaskMock(0, context);
		PerfTrackingTaskProxy proxy = new PerfTrackingTaskProxy(task);
		for (int i = 0; i < 100; i++) {
			assertEquals(TaskResult.EXECUTING, proxy.execute(context, ErrorHandler.getDefault()));
		}
		LatencyCounter counter = proxy.getTracker().getCounter();
		assertEquals(100, counter.sampleCount());
		proxy.close();
	}
	
}

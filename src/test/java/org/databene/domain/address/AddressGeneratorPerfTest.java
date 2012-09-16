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

package org.databene.domain.address;

import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests the performance of the {@link AddressGenerator}.<br/><br/>
 * Created: 17.04.2010 07:12:51
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class AddressGeneratorPerfTest {
	
	@Rule public ContiPerfRule rule = new ContiPerfRule();

	private AddressGenerator generatorDE;
	
	@Before
	public void setUp() {
		generatorDE = new AddressGenerator(Country.GERMANY.getIsoCode());
		generatorDE.init(new DefaultBeneratorContext());
	}
	
	@After
	public void tearDown() {
		generatorDE.close();
	}
	
	/** Verifies that the {@link AddressGenerator} generates at least 1000 addresses per second */
	@Test
	@PerfTest(invocations = 1000)
	@Required(throughput = 1000)
	public void test_DE() {
		generatorDE.generate();
	}
	
}

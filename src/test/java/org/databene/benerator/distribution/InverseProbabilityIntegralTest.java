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

package org.databene.benerator.distribution;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.databene.benerator.Generator;
import org.databene.benerator.SequenceTestGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.junit.Test;

/**
 * Tests the {@link CumulativeDistributionFunction}.<br/><br/>
 * Created: 12.03.2010 15:06:33
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class InverseProbabilityIntegralTest extends GeneratorTest {

	private Fcn fcn = new Fcn();

	@Test(expected = IllegalArgumentException.class)
	public void testCreateDoubleGenerator_unique() {
		fcn.createNumberGenerator(Double.class, 1., 4., 0.5, true);
	}
	
	@Test
	public void testCreateDoubleGenerator_notUnique() {
		Generator<Double> generator = fcn.createNumberGenerator(Double.class, 1., 4., 0.5, false);
		generator.init(context);
		int n = 1000;
		Map<Double, AtomicInteger> counts = countProducts(generator, n);
		assertEquals(7, counts.size());
		for (double d = 1; d <= 4; d += 0.5)
			assertEquals(1./7, counts.get(d).doubleValue() / n, 0.05);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testApply_unique() {
		Generator<String> source = new SequenceTestGenerator<String>("A", "B");
		source.init(context);
		fcn.applyTo(source, true);
	}
	
	@Test
	public void testApply_notUnique() {
		Generator<String> source = new SequenceTestGenerator<String>("A", "B");
		source.init(context);
		Generator<String> generator = fcn.applyTo(source, false);
		generator.init(context);
		int n = 1000;
		Map<String, AtomicInteger> counts = countProducts(generator, n);
		assertEquals(2, counts.size());
		assertEquals(0.5, counts.get("A").doubleValue() / n, 0.05);
		assertEquals(0.5, counts.get("B").doubleValue() / n, 0.05);
	}
	
	static class Fcn extends CumulativeDistributionFunction {
		
		@Override
        public double inverse(double probability) {
	        return probability * 8;
        }

		@Override
        public double cumulativeProbability(double value) {
	        return value / 8;
        }

	}

}

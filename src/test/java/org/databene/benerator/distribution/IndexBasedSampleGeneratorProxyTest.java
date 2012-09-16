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

import static org.junit.Assert.assertEquals;

import org.databene.benerator.Generator;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.SequenceTestGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.wrapper.WrapperFactory;
import org.junit.Test;

/**
 * Tests the {@link IndexBasedSampleGeneratorProxy}.<br/><br/>
 * Created: 21.07.2010 07:09:23
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class IndexBasedSampleGeneratorProxyTest extends GeneratorTest {

	@Test
	public void testSourceHandling() {
		SequenceTestGenerator<Integer> source = new SequenceTestGenerator<Integer>(1, 2, 3);
		Distribution distribution = new TestDistribution();
		NonNullGenerator<Integer> generator = WrapperFactory.asNonNullGenerator(
				new IndexBasedSampleGeneratorProxy<Integer>(source, distribution, false));
		
		// on initialization, DistributingSampleGeneratorProxy scans throug all available 3 source values 
		// plus a single call that returns null for signaling unavailability
		generator.init(context);
		assertEquals(4, source.generateCount);
		assertEquals(new Integer(1), generator.generate());
		assertEquals(4, source.generateCount);

		// on reset(), the source must be scanned once more
		generator.reset();
		assertEquals(1, source.resetCount);
		assertEquals(8, source.generateCount);
		assertEquals(new Integer(1), generator.generate());
		
		// on close(), the source must be closed too
		generator.close();
		assertEquals(8, source.generateCount);
		assertEquals(1, source.closeCount);
	}
	
	public static class TestDistribution implements Distribution {

		public <T> Generator<T> applyTo(Generator<T> source, boolean unique) {
	        throw new UnsupportedOperationException("not implemented");
        }

		@SuppressWarnings("unchecked")
        public <T extends Number> NonNullGenerator<T> createNumberGenerator(Class<T> numberType, T min, T max, T granularity,
                boolean unique) {
	        return (NonNullGenerator<T>) WrapperFactory.asNonNullGenerator(new SequenceTestGenerator<Integer>(0, 1, 2));
        }
	}
	
}

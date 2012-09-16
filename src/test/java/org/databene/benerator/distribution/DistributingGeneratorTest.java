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

import org.databene.benerator.Generator;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.SequenceTestGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.wrapper.WrapperFactory;
import org.junit.Test;

/**
 * Tests the {@link DistributingGenerator}.<br/><br/>
 * Created: 21.07.2010 06:54:40
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class DistributingGeneratorTest extends GeneratorTest {

	@Test
	public void test() {
		SequenceTestGenerator<Integer> source = new SequenceTestGenerator<Integer>(1, 2, 3);
		Distribution distribution = new TestDistribution();
		NonNullGenerator<Integer> generator = WrapperFactory.asNonNullGenerator(
				new DistributingGenerator<Integer>(source, distribution, false));
		generator.init(context);
		assertEquals(new Integer(1), generator.generate());
		generator.reset();
		assertEquals(1, source.resetCount);
		assertEquals(new Integer(1), generator.generate());
		generator.close();
		assertEquals(2, source.generateCount);
		assertEquals(1, source.closeCount);
	}
	
	public static class TestDistribution implements Distribution {

		public <T> Generator<T> applyTo(Generator<T> source, boolean unique) {
	        return source;
        }

		public <T extends Number> NonNullGenerator<T> createNumberGenerator(Class<T> numberType, T min, T max, T granularity,
                boolean unique) {
	        throw new UnsupportedOperationException("not implemented");
        }
		
	}
	
}

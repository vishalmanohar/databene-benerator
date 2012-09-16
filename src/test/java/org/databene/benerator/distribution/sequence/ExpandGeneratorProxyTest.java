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

package org.databene.benerator.distribution.sequence;

import static org.junit.Assert.*;

import org.databene.benerator.Generator;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.SequenceTestGenerator;
import org.databene.benerator.UniqueLongValidator;
import org.databene.benerator.primitive.IncrementGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.wrapper.WrapperFactory;
import org.junit.Test;

/**
 * Tests the {@link ExpandGeneratorProxy}.<br/><br/>
 * Created: 10.12.2009 15:53:20
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ExpandGeneratorProxyTest extends GeneratorTest {
	
	private static final int N = 100;
	private static final int CACHE_SIZE = 30;
	private static final int BUCKET_SIZE = 10;

	@Test(expected = InvalidGeneratorSetupException.class)
	public void testNullSource() {
		Generator<Long> generator = new ExpandGeneratorProxy<Long>(null, CACHE_SIZE, BUCKET_SIZE);
		generator.init(context);
	}
	
	@Test
	public void testEmptySource() {
		SequenceTestGenerator<Long> emptyFeed = new SequenceTestGenerator<Long>();
		Generator<Long> generator = new ExpandGeneratorProxy<Long>(emptyFeed, CACHE_SIZE, BUCKET_SIZE);
		generator.init(context);
		assertUnavailable(generator);
	}
	
	@Test
	public void testNormal() {
		Generator<Long> feed = new IncrementGenerator(1, 1, N);
		NonNullGenerator<Long> generator = WrapperFactory.asNonNullGenerator(
			ExpandGeneratorProxy.uniqueProxy(feed, CACHE_SIZE, BUCKET_SIZE));
		generator.init(context);
		UniqueLongValidator validator = new UniqueLongValidator(N);
		for (int i = 0; i < N; i++) {
			Long product = generator.generate();
			assertNotNull(product);
			assertTrue("Not unique: " + product, validator.valid(product));
			assertTrue(product <= N);
			assertTrue(product >= 1);
		}
		assertUnavailable(generator);
	}
	
}

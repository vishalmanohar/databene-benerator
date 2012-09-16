/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.factory;

import java.util.Date;
import java.util.Set;

import org.databene.benerator.Generator;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.commons.CollectionUtil;
import org.databene.commons.Period;
import org.databene.commons.TimeUtil;
import org.databene.model.data.Uniqueness;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Test the {@link CoverageGeneratorFactory}.<br/><br/>
 * Created: 21.08.2011 06:00:54
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class CoverageGeneratorFactoryTest extends GeneratorTest {
	
	private CoverageGeneratorFactory factory = new CoverageGeneratorFactory();

	@Test
	public void testCreateDateGenerator() {
		Date AUGUST_20_2011 = TimeUtil.date(2011, 7, 20);
		Date AUGUST_22_2011 = TimeUtil.date(2011, 7, 22);
		Date AUGUST_24_2011 = TimeUtil.date(2011, 7, 24);
		Generator<Date> generator = factory.createDateGenerator(
				AUGUST_20_2011, AUGUST_24_2011, 2 * Period.DAY.getMillis(), null);
		GeneratorUtil.init(generator);
		expectGeneratedSequence(generator, AUGUST_20_2011, AUGUST_22_2011, AUGUST_24_2011);
	}
	
	@Test
	public void testCreateNumberGenerator() {
		NonNullGenerator<Integer> generator = factory.createNumberGenerator(
				Integer.class, 1, true, 5, true, 2, null, Uniqueness.NONE);
		GeneratorUtil.init(generator);
		expectGeneratedSequence(generator, 1, 3, 5);
	}
	
	@Test
	public void testCreateStringGenerator() {
		NonNullGenerator<String> generator = factory.createStringGenerator(
				CollectionUtil.toSet('A', 'B', 'C'), 1, 3, 2, null, Uniqueness.NONE);
		GeneratorUtil.init(generator);
		expectGeneratedSequence(generator, "A", "B", "C", "AAA", "BBB", "CCC");
	}
	
	@Test
	public void testDefaultSubSet() {
		Set<Character> chars = CollectionUtil.toSet('A', 'B', 'C');
		assertEquals(chars, factory.defaultSubSet(chars));
	}
	
	@Test
	public void testDefaultCounts() {
		Set<Integer> defaultCounts = factory.defaultCounts(1, 5, 2);
		assertEquals(CollectionUtil.toSet(1, 3, 5), defaultCounts);
	}
	
	@Test
	public void testDefaultDistribution() {
		assertEquals(SequenceManager.STEP_SEQUENCE, factory.defaultDistribution(Uniqueness.NONE));
	}
	
}

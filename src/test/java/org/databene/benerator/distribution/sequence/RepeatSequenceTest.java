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

package org.databene.benerator.distribution.sequence;

import java.math.BigDecimal;

import org.databene.benerator.Generator;
import org.databene.benerator.SequenceTestGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.junit.Test;

/**
 * Tests the {@link RepeatSequence}.<br/><br/>
 * Created: 12.02.2010 12:28:43
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class RepeatSequenceTest extends GeneratorTest {

	@Test
	public void testCreateSequence() {
		RepeatSequence sequence = createSequence122333();
		Generator<Integer> generator = sequence.createNumberGenerator(Integer.class, 1, 3, 1, false);
		generator.init(context);
		expectGeneratedSequence(generator, 1, 2, 2, 3, 3, 3).withCeasedAvailability();
	}

	@Test
	public void testApplyTo() {
        Generator<Integer> source = new SequenceTestGenerator<Integer>(1, 2, 3);
		RepeatSequence sequence = createSequence122333();
		Generator<Integer> generator = sequence.applyTo(source, false);
		generator.init(context);
		expectGeneratedSequence(generator, 1, 2, 2, 3, 3, 3).withCeasedAvailability();
	}
	
	private RepeatSequence createSequence122333() {
	    StepSequence repetitionDistribution = new StepSequence(BigDecimal.ONE, BigDecimal.ZERO);
		return new RepeatSequence(0, 2, 1, repetitionDistribution);
    }
	
}

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

package org.databene.benerator.distribution.sequence;

import java.math.BigDecimal;

import org.databene.benerator.Generator;
import org.databene.benerator.SequenceTestGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.junit.Test;

/**
 * Tests the {@link StepSequence}.<br/><br/>
 * Created: 20.07.2010 23:30:08
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class StepSequenceTest extends GeneratorTest {

	@Test
    public void testApplyTo_limit() throws Exception {
        expectGeneratedSequence(apply(1L, 1L, 1L),  1).withCeasedAvailability();
	}

	@Test
    public void testCreateGenerator_limit() throws Exception {
        expectGeneratedSequence(numberGen(1L, 1L, 1L),  1L).withCeasedAvailability();
	}

    private Generator<Integer> apply(long initial, long increment, long limit) {
    	Generator<Integer> source = new SequenceTestGenerator<Integer>(1, 2, 3);
        StepSequence sequence = new StepSequence(
        		new BigDecimal(initial), new BigDecimal(increment), new BigDecimal(limit));
		Generator<Integer> generator = sequence.applyTo(source, false);
		return initialize(generator);
    }

    private Generator<Long> numberGen(long initial, long increment, long limit) {
        StepSequence sequence = new StepSequence(
        		new BigDecimal(initial), new BigDecimal(increment), new BigDecimal(limit));
		Generator<Long> generator = sequence.createNumberGenerator(Long.class, initial, limit, increment, false);
		return initialize(generator);
    }

}

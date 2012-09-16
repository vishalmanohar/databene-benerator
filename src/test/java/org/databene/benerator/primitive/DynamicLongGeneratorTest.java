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

package org.databene.benerator.primitive;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.databene.benerator.Generator;
import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.test.GeneratorTest;
import org.databene.commons.Context;
import org.databene.script.Expression;
import org.databene.script.expression.DynamicExpression;
import org.databene.script.expression.ExpressionUtil;
import org.junit.Test;

/**
 * Tests the {@link DynamicLongGenerator}.<br/><br/>
 * Created: 28.03.2010 12:36:26
 * @since 0.6.1
 * @author Volker Bergmann
 */
public class DynamicLongGeneratorTest extends GeneratorTest {

	private static final Expression<Long> ONE = ExpressionUtil.constant(1L);
	private static final Expression<Long> TWO = ExpressionUtil.constant(2L);
	private static final Expression<Long> THREE = ExpressionUtil.constant(3L);
	private static final Expression<Sequence> RANDOM_SEQUENCE = ExpressionUtil.constant(SequenceManager.RANDOM_SEQUENCE);
	private static final Expression<Sequence> STEP_SEQUENCE = ExpressionUtil.constant(SequenceManager.STEP_SEQUENCE);
	private static final Expression<Boolean> NOT_UNIQUE = ExpressionUtil.constant(false);

	@Test
	public void testConstant() {
		Generator<Long> generator = new DynamicLongGenerator(ONE, THREE, TWO, RANDOM_SEQUENCE, NOT_UNIQUE);
		generator.init(context);
		Map<Long, AtomicInteger> productCounts = countProducts(generator, 100);
		assertEquals(2, productCounts.size());
		assertTrue(productCounts.containsKey(1L));
		assertTrue(productCounts.containsKey(3L));
	}

	@Test
	public void testLifeCycle() {
		Generator<Long> generator = new DynamicLongGenerator(new IncrementExpression(1), new IncrementExpression(2), 
				ONE, STEP_SEQUENCE, NOT_UNIQUE);
		generator.init(context); // min==1, max==2
		expectGeneratedSequenceOnce(generator, 1L, 2L);
		generator.reset(); // min==2, max==3
		expectGeneratedSequenceOnce(generator, 2L, 3L);
	}
	
	static class IncrementExpression extends DynamicExpression<Long> {
		
		private long value;
		
		public IncrementExpression(long value) {
	        this.value = value;
        }

		public Long evaluate(Context context) {
	        return value++;
        }
	}
	
}

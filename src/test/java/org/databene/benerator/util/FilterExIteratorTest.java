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

package org.databene.benerator.util;

import static org.junit.Assert.*;

import org.databene.commons.CollectionUtil;
import org.databene.commons.Context;
import org.databene.commons.context.DefaultContext;
import org.databene.script.Expression;
import org.databene.script.expression.DynamicExpression;
import org.databene.webdecs.DataContainer;
import org.databene.webdecs.DataIterator;
import org.databene.webdecs.util.DataIteratorFromJavaIterator;
import org.junit.Test;

/**
 * Tests the {@link FilterExIterator}.<br/><br/>
 * Created: 08.03.2011 14:24:18
 * @since 0.5.8
 * @author Volker Bergmann
 */
public class FilterExIteratorTest {

	@Test
	public void test() {
		Context context = new DefaultContext();
		Expression<Boolean> expression = new IsThreeExpression();
		DataIterator<Integer> source = new DataIteratorFromJavaIterator<Integer>(
				CollectionUtil.toList(2, 3, 4).iterator(), Integer.class);
		FilterExIterator<Integer> iterator = new FilterExIterator<Integer>(source, expression, context);
		assertEquals(3, iterator.next(new DataContainer<Integer>()).getData().intValue());
		assertNull(iterator.next(new DataContainer<Integer>()));
	}
	
	class IsThreeExpression extends DynamicExpression<Boolean> {

		public Boolean evaluate(Context context) {
			Integer candidateValue = (Integer) context.get("_candidate");
			return (candidateValue != null && candidateValue.intValue() == 3);
		}

	}

}

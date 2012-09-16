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

package org.databene.benerator.engine;

import static org.junit.Assert.*;

import java.util.List;

import org.databene.benerator.test.BeneratorIntegrationTest;
import org.databene.benerator.test.ConsumerMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Benerator integration test for array generation.<br/><br/>
 * Created: 08.08.2011 16:57:47
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class ArrayGenerationIntegrationTest extends BeneratorIntegrationTest {

	private ConsumerMock consumer;

	@Before
	public void setUpConsumer() throws Exception {
		consumer = new ConsumerMock(true);
		context.set("cons", consumer);
	}
	
	@Test
	public void testSimpleCase() {
		parseAndExecute(
				"<generate count='2' consumer='cons'>" +
				"	<value type='int' constant='3'/>" +
				"	<value type='string' constant='x'/>" +
				"</generate>");
		List<Object[]> products = getConsumedEntities();
		assertEquals(2, products.size());
		for (Object[] product : products) {
			assertEquals(2, product.length);
			assertEquals(3, product[0]);
			assertEquals("x", product[1]);
		}
	}
	/* TODO v0.7.1 make it work
	@Test
	public void testVariable() {
		parseAndExecute(
				"<generate count='2' consumer='cons'>" +
				"	<variable name='aVar' constant='4' />" +
				"	<value type='int' script='aVar'/>" +
				"	<value type='string' constant='x'/>" +
				"</generate>");
		List<Object[]> products = getConsumedEntities();
		assertEquals(2, products.size());
		for (Object[] product : products) {
			assertEquals(2, product.length);
			assertEquals(4, product[0]);
			assertEquals("x", product[1]);
		}
	}
	*/
	
	// helpers ---------------------------------------------------------------------------------------------------------
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected List<Object[]> getConsumedEntities() {
		return (List) consumer.getProducts();
	}
}

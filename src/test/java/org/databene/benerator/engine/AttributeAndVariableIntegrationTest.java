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

package org.databene.benerator.engine;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.databene.benerator.test.BeneratorIntegrationTest;
import org.databene.benerator.test.ConsumerMock;
import org.databene.benerator.test.PersonSource;
import org.databene.model.data.Entity;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests &lt;variable&gt; support in Benerator descriptor files.<br/><br/>
 * Created: 10.08.2010 07:06:46
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class AttributeAndVariableIntegrationTest extends BeneratorIntegrationTest {

	private ConsumerMock consumer;
	
    @Before
	public void setUpSourceAndConsumer() throws Exception {
		consumer = new ConsumerMock(true);
		context.set("cons", consumer);
		PersonSource pit = new PersonSource();
		pit.setContext(context);
		context.set("pit", pit);
	}
	
	// test methods ----------------------------------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	@Test
	public void testIterateThis() {
		parseAndExecute(
				"<iterate type='referer' source='pit' consumer='cons'>" +
				"	<variable name='_n' script='this.name' converter='org.databene.commons.converter.ToUpperCaseConverter' />" +
				"	<attribute name='name' script='_n' />" +
				"</iterate>");
		List<Entity> products = (List<Entity>) consumer.getProducts();
		assertEquals(2, products.size());
		assertEquals("ALICE", products.get(0).get("name"));
		assertEquals(23,      products.get(0).get("age"));
		assertEquals("BOB",   products.get(1).get("name"));
		assertEquals(34,      products.get(1).get("age"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAttributeFromVariable() {
		parseAndExecute(
				"<generate type='testAttributeFromVariable' count='2' consumer='cons'>" +
				"	<variable name='_n' type='int' distribution='increment' />" +
				"	<attribute name='x' type='int' script='_n + 1' />" +
				"</generate>");
		List<Entity> products = (List<Entity>) consumer.getProducts();
		assertEquals(2, products.size());
		assertEquals(2, products.get(0).get("x"));
		assertEquals(3, products.get(1).get("x"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testVariableFromAttribute_refByThis() {
		parseAndExecute(
				"<generate type='testVariableFromAttribute_refByThis' count='2' consumer='cons'>" +
				"	<attribute name='x' type='int' distribution='increment' />" +
				"	<attribute name='y' type='int' script='this.x + 3' />" +
				"</generate>");
		List<Entity> products = (List<Entity>) consumer.getProducts();
		assertEquals(2, products.size());
		assertEquals(1, products.get(0).get("x"));
		assertEquals(4, products.get(0).get("y"));
		assertEquals(2, products.get(1).get("x"));
		assertEquals(5, products.get(1).get("y"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testVariableFromAttribute_refByTypeName() {
		parseAndExecute(
				"<generate type='testVariableFromAttribute_refByTypeName' count='2' consumer='cons'>" +
				"	<attribute name='x' type='int' distribution='increment' />" +
				"	<attribute name='y' type='int' script='testVariableFromAttribute_refByTypeName.x + 3' />" +
				"</generate>");
		List<Entity> products = (List<Entity>) consumer.getProducts();
		assertEquals(2, products.size());
		assertEquals(1, products.get(0).get("x"));
		assertEquals(4, products.get(0).get("y"));
		assertEquals(2, products.get(1).get("x"));
		assertEquals(5, products.get(1).get("y"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testVariableFromAttribute_refByInstanceName() {
		parseAndExecute(
				"<generate name='xyz' type='testVariableFromAttribute_refByInstanceName' count='2' consumer='cons'>" +
				"	<attribute name='x' type='int' distribution='increment' />" +
				"	<attribute name='y' type='int' script='xyz.x + 3' />" +
				"</generate>");
		List<Entity> products = (List<Entity>) consumer.getProducts();
		assertEquals(2, products.size());
		assertEquals(1, products.get(0).get("x"));
		assertEquals(4, products.get(0).get("y"));
		assertEquals(2, products.get(1).get("x"));
		assertEquals(5, products.get(1).get("y"));
	}

	@Test
	public void testVarAfterSubGen() {
		parseAndExecute(
				"<generate name='e' type='entity' count='1'>" +
				"	<generate name='sub' count='3'>" +
				"		<attribute name='x' type='int' generator='IncrementGenerator' />" +
				"	</generate>" +
				"	<variable name='y' type='int' script='sub.x'/>" +
				"	<evaluate id='res' assert='result==3'>{y}</evaluate>" +
				"	<attribute name='val' type='int' script='res'/>" +
				"</generate>");
		Entity e = (Entity) context.get("e");
		assertEquals(3, e.get("val"));
	}
	
	@Test
	public void testEvaluateBetweenAttributes() {
		parseAndExecute(
				"<generate name='e' type='entity' count='1'>" +
				"	<evaluate id='val1' assert='result==null'>this.id</evaluate>" + 
				"	<id name='id' type='int'/>" +
				"	<evaluate id='val2' assert='result==1'>this.id</evaluate>" +
				"	<attribute name='x' type='int' script='val2' />" + 
				"</generate>");
		Entity e = (Entity) context.get("e");
		assertEquals(1, e.get("x"));
	}
	
	@Test
	public void testAttributeAfterSubGen() {
		parseAndExecute(
				"<generate count='5'>" +
				"	<generate count='3'/>" +
				"	<attribute name='y' type='int'/>" +
				"</generate>");
	}
	
}

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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import org.databene.benerator.composite.ComponentBuilder;
import org.databene.benerator.primitive.HibUUIDGenerator;
import org.databene.benerator.primitive.IncrementGenerator;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.model.data.Entity;
import org.databene.model.data.IdDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.Uniqueness;
import org.junit.Test;

/**
 * Tests the id builder creation of the {@link ComponentBuilderFactory}.<br/><br/>
 * Created: 06.07.2011 15:46:50
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class ComponentBuilderFactory_IdTest extends AbstractComponentBuilderFactoryTest {

    @Test
	public void testDefault() {
		IdDescriptor id = createId("id", "int");
		ComponentBuilder<Entity> generator = createAndInitBuilder(id);
		Entity entity = createEntity("Person");
		setCurrentProduct(entity);
		generator.execute(context);
		assertEquals(1, entity.get("id"));
	}
    
	/**
	 * Tests UUID generation
	 * <id name="id" strategy="uuid"/>
	 */
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public void testUuid() {
		String componentName = "id";
		SimpleTypeDescriptor type = createSimpleType("idType", "string");
		type.setGenerator(HibUUIDGenerator.class.getName());
		IdDescriptor id = createId(componentName, type);
		ComponentBuilder builder = createComponentBuilder(id);
		ComponentBuilderGenerator<String> helper = new ComponentBuilderGenerator(builder, componentName);
		helper.init(context);
		expectUniqueGenerations(helper, 10);
	}
	
	/**
	 * Tests 'increment' id generation with unspecified type
	 * <id name="id" strategy="increment"/>
	 */
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public void testIncrementIdWithoutType() {
		String componentName = "id";
		SimpleTypeDescriptor type = createSimpleType("idType", "long");
		type.setGenerator(IncrementGenerator.class.getName());
		IdDescriptor id = createId(componentName, type);
		ComponentBuilder builder = createComponentBuilder(id);
		ComponentBuilderGenerator<Long> helper = new ComponentBuilderGenerator(builder, componentName);
		helper.init(context);
		expectGeneratedSequenceOnce(helper, 1L, 2L, 3L, 4L);
	}
	
	/**
	 * Tests id generation with unspecified type and strategy
	 * <id name="id"/>
	 */
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public void testDefaultIdGeneration() {
		String componentName = "id";
		IdDescriptor id = createId(componentName);
		ComponentBuilder builder = createComponentBuilder(id);
		ComponentBuilderGenerator<Long> helper = new ComponentBuilderGenerator(builder, componentName);
		helper.init(context);
		expectGeneratedSequenceOnce(helper, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
		assertNotNull(helper.generate(new ProductWrapper<Long>()));
	}
	
	/**
	 * Tests 'increment' id generation with 'byte' type
	 * <id name="id" type="byte" strategy="increment"/>
	 */
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public void testIncrementByteId() {
		String componentName = "id";
		SimpleTypeDescriptor type = createSimpleType("idType", "byte");
		type.setGenerator(IncrementGenerator.class.getName());
		IdDescriptor id = createId(componentName, type);
		ComponentBuilder builder = createComponentBuilder(id);
		ComponentBuilderGenerator<Byte> helper = new ComponentBuilderGenerator(builder, componentName);
		helper.init(context);
		expectGeneratedSequenceOnce(helper, (byte) 1, (byte) 2, (byte) 3, (byte) 4);
	}
	
    @SuppressWarnings("unchecked")
	private ComponentBuilder<Entity> createAndInitBuilder(IdDescriptor id) {
		ComponentBuilder<Entity> builder = (ComponentBuilder<Entity>) ComponentBuilderFactory.createComponentBuilder(
				id, Uniqueness.NONE, context);
		builder.init(context);
		return builder;
	}

}

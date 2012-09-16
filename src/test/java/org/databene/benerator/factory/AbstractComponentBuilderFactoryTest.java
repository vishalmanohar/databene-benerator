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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.composite.ComponentBuilder;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.util.AbstractGenerator;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.PartDescriptor;
import org.databene.model.data.Uniqueness;

/**
 * Abstract test parent class which provides utility methods for testing the {@link ComponentBuilderFactory}.<br/><br/>
 * Created: 15.07.2011 18:21:19
 * @since 0.7.0
 * @author Volker Bergmann
 */
public abstract class AbstractComponentBuilderFactoryTest extends GeneratorTest {

	// private helpers -------------------------------------------------------------------------------------------------
	
	protected ComponentBuilder<Entity> createComponentBuilder(ComponentDescriptor component) {
		return createComponentBuilder(component, new DefaultBeneratorContext());
	}
	
	@SuppressWarnings("unchecked")
    protected ComponentBuilder<Entity> createComponentBuilder(ComponentDescriptor component, BeneratorContext context) {
		return (ComponentBuilder<Entity>) ComponentBuilderFactory.createComponentBuilder(component, Uniqueness.NONE, context);
	}
	
	@SuppressWarnings("unchecked")
	public final class ComponentBuilderGenerator<E> extends AbstractGenerator<E> {
		
        @SuppressWarnings("rawtypes")
		private ComponentBuilder builder;
		private String componentName;

        @SuppressWarnings("rawtypes")
		public ComponentBuilderGenerator(ComponentBuilder builder, String componentName) {
			this.builder = builder;
			this.componentName = componentName;
		}

		@Override
        public void init(GeneratorContext context) throws InvalidGeneratorSetupException {
	        builder.init((BeneratorContext) context);
	        super.init(context);
        }

        public Class<E> getGeneratedType() {
	        return (Class<E>) Object.class;
        }

		@SuppressWarnings("synthetic-access")
        public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
			Entity entity = createEntity("Test");
			context.setCurrentProduct(new ProductWrapper<Entity>(entity));
			if (!builder.execute((BeneratorContext) context))
				return null;
			return wrapper.wrap((E) entity.get(componentName));
		}

		@Override
		public void reset() {
			super.reset();
			builder.reset();
		}
		
		@Override
		public void close() {
			super.close();
			builder.close();
		}

		public boolean isParallelizable() {
	        return false;
        }

		public boolean isThreadSafe() {
	        return false;
        }

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    protected <T> void expectUniqueSequence(PartDescriptor name, T... products) {
		ComponentBuilder builder = createComponentBuilder(name);
		Generator<T> helper = new ComponentBuilderGenerator(builder, name.getName());
		helper.init(context);
		expectGeneratedSequence(helper, products).withCeasedAvailability();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    protected <T> void expectUniqueSet(PartDescriptor name, T... products) {
		ComponentBuilder builder = createComponentBuilder(name);
		Generator<T> helper = new ComponentBuilderGenerator(builder, name.getName());
		helper.init(context);
		expectUniquelyGeneratedSet(helper, products).withCeasedAvailability();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
    protected <T> void expectSet(PartDescriptor name, int n, T... products) {
		ComponentBuilder builder = createComponentBuilder(name);
		Generator<T> helper = new ComponentBuilderGenerator(builder, name.getName());
		helper.init(context);
		expectGeneratedSet(helper, n, products);
	}
	
	/*
	private <T> void expectSequence(PartDescriptor name, T... products) {
		ComponentBuilder builder = createComponentBuilder(name);
		Generator<T> helper = new ComponentBuilderGenerator(builder, name.getName());
		expectGeneratedSet(helper, products).withContinuedAvailability();
	}
	*/

	protected void expectNullGenerations(ComponentBuilderGenerator<String> gen, int n) {
	    ProductWrapper<String> wrapper = new ProductWrapper<String>();
	    for (int i = 0; i < n; i++) {
	    	wrapper = gen.generate(wrapper);
	    	assertNotNull(wrapper);
	    	assertNull(wrapper.unwrap());
	    }
    }
	
	enum TestEnum {
		firstInstance
	}

}

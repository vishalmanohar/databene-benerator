/*
 * (c) Copyright 2010-2012 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.composite;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.util.WrapperProvider;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.Mutator;

/**
 * Helper class for simple definition of custom {@link ComponentBuilder}s which uses a {@link Mutator}
 * Created: 30.04.2010 09:34:42
 * @since 0.6.1
 * @author Volker Bergmann
 */
public abstract class AbstractComponentBuilder<E> extends AbstractGeneratorComponent<E> implements ComponentBuilder<E> {

	protected Mutator mutator;
	private WrapperProvider<Object> productWrapper = new WrapperProvider<Object>();
	
    public AbstractComponentBuilder(Generator<?> source, Mutator mutator, String scope) {
		super(source, scope);
		this.mutator = mutator;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean execute(BeneratorContext context) {
		message = null;
		Object target = context.getCurrentProduct().unwrap();
		ProductWrapper<?> wrapper = source.generate((ProductWrapper) productWrapper.get());
		if (wrapper == null) {
			message = "Generator unavailable: " + source;
			return false;
		}
		mutator.setValue(target, wrapper.unwrap());
		return true;
	}

}

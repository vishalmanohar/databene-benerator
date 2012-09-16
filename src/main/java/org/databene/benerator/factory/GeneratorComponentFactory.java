/*
 * (c) Copyright 2011-2012 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.composite.GeneratorComponent;
import org.databene.benerator.composite.Variable;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.model.data.ArrayElementDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.Uniqueness;
import org.databene.model.data.VariableDescriptor;

/**
 * Factory for {@link GeneratorComponent}s.<br/><br/>
 * Created: 08.08.2011 12:04:39
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class GeneratorComponentFactory {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static GeneratorComponent<?> createGeneratorComponent(InstanceDescriptor descriptor, Uniqueness ownerUniqueness, BeneratorContext context) {
		if (descriptor instanceof ComponentDescriptor)
			return ComponentBuilderFactory.createComponentBuilder((ComponentDescriptor) descriptor, ownerUniqueness, context);
		else if (descriptor instanceof VariableDescriptor)
			return new Variable(descriptor.getName(), VariableGeneratorFactory.createGenerator((VariableDescriptor) descriptor, context), descriptor.getTypeDescriptor().getScope());
		else if (descriptor instanceof ArrayElementDescriptor)
			return ComponentBuilderFactory.createComponentBuilder((ArrayElementDescriptor) descriptor, ownerUniqueness, context);
		else
			throw new UnsupportedOperationException("Not a supported generator compnent type: " + descriptor.getClass());
	}
}

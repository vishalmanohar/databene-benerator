/*
 * (c) Copyright 2007-2011 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
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

import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.IdDescriptor;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.Mode;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.data.Uniqueness;
import org.databene.benerator.*;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.primitive.IncrementGenerator;

/**
 * Creates entity generators from entity metadata.<br/>
 * <br/>
 * Created: 08.09.2007 07:45:40
 * @author Volker Bergmann
 */
public class InstanceGeneratorFactory {

    // protected constructor for preventing instantiation --------------------------------------------------------------
    
    protected InstanceGeneratorFactory() {}

    public static Generator<?> createSingleInstanceGenerator(
            InstanceDescriptor descriptor, Uniqueness ownerUniqueness, BeneratorContext context) {
        Generator<?> generator = DescriptorUtil.createNullQuotaOneGenerator(descriptor, context);
        if (generator != null)
        	return generator;
        Uniqueness uniqueness = DescriptorUtil.getUniqueness(descriptor, context);
        if (!uniqueness.isUnique())
        	uniqueness = ownerUniqueness;
		boolean nullable = DescriptorUtil.isNullable(descriptor, context);
		TypeDescriptor type = descriptor.getTypeDescriptor();
		String instanceName = descriptor.getName();
		if (descriptor.getMode() == Mode.ignored)
			return null;
		if (type != null) {
			generator = MetaGeneratorFactory.createTypeGenerator(type, instanceName, nullable, uniqueness, context);
		} else {
        	ComponentDescriptor defaultConfig = context.getDefaultComponentConfig(instanceName);
        	if (defaultConfig != null)
        		return createSingleInstanceGenerator(defaultConfig, ownerUniqueness, context);
        	if (nullable && DescriptorUtil.shouldNullifyEachNullable(descriptor, context))
        		return createNullGenerator(descriptor, context);
        	if (descriptor instanceof IdDescriptor)
				generator = new IncrementGenerator(1);
        	else
        		throw new UnsupportedOperationException("Type of " + instanceName + " is not defined");
        }
		GeneratorFactory generatorFactory = context.getGeneratorFactory();
		generator = generatorFactory.applyNullSettings(generator, nullable, descriptor.getNullQuota());
		return generator;
    }
    
    public static Generator<?> createConfiguredDefaultGenerator(String componentName, Uniqueness ownerUniqueness, BeneratorContext context) {
    	ComponentDescriptor defaultConfig = context.getDefaultComponentConfig(componentName);
    	if (defaultConfig != null)
    		return createSingleInstanceGenerator(defaultConfig, ownerUniqueness, context);
    	return null;
    }

	protected static Generator<?> createNullGenerator(InstanceDescriptor descriptor, BeneratorContext context) {
		Class<?> generatedType;
		TypeDescriptor typeDescriptor = descriptor.getTypeDescriptor();
		if (typeDescriptor instanceof SimpleTypeDescriptor)
			generatedType = ((SimpleTypeDescriptor) typeDescriptor).getPrimitiveType().getJavaType();
		else
			generatedType = String.class;
		return context.getGeneratorFactory().createNullGenerator(generatedType); 
    }

}

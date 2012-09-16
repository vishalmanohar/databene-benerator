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

package org.databene.platform.array;

import org.databene.commons.ArrayFormat;
import org.databene.commons.Converter;
import org.databene.commons.Escalator;
import org.databene.commons.LoggerEscalator;
import org.databene.commons.Mutator;
import org.databene.commons.StringUtil;
import org.databene.commons.converter.ConverterManager;
import org.databene.commons.converter.NoOpConverter;
import org.databene.commons.converter.ThreadSafeConverter;
import org.databene.commons.converter.AnyConverter;
import org.databene.commons.mutator.ConvertingMutator;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.EntityGraphMutator;
import org.databene.model.data.SimpleTypeDescriptor;

/**
 * Converts an array of feature values to an entity.<br/>
 * <br/>
 * Created: 26.08.2007 12:27:45
 * @author Volker Bergmann
 */
public class Array2EntityConverter extends ThreadSafeConverter<Object[], Entity> {
	
    private ComplexTypeDescriptor descriptor;
    
    private String[] featureNames;
    
	private Mutator[] entityMutators;
    
    protected Escalator escalator = new LoggerEscalator();

    public Array2EntityConverter(ComplexTypeDescriptor descriptor, String[] featureNames, boolean stringSource) {
    	super(Object[].class, Entity.class);
        this.descriptor = descriptor;
        this.featureNames = featureNames;
        this.entityMutators = new Mutator[featureNames.length];
        for (int i = 0; i < featureNames.length; i++)
        	this.entityMutators[i] = createMutator(featureNames[i], descriptor, stringSource);
    }

	@SuppressWarnings("rawtypes")
	protected ConvertingMutator createMutator(
			String featureName, ComplexTypeDescriptor descriptor, boolean stringSource) {
		Converter converter = createConverter(featureName, descriptor, stringSource);
		Mutator mutator = new EntityGraphMutator(featureName, descriptor);
		return new ConvertingMutator(mutator, converter);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Converter createConverter(
			String featureName, ComplexTypeDescriptor descriptor, boolean stringSource) {
		Converter converter = new NoOpConverter();
		while (featureName.contains(".")) {
			String[] pathComponents = StringUtil.splitOnFirstSeparator(featureName, '.');
			String partName = pathComponents[0];
			ComponentDescriptor component = descriptor.getComponent(partName);
			descriptor = (ComplexTypeDescriptor) component.getTypeDescriptor();
			featureName = pathComponents[1];
		}
		if (descriptor != null) {
			ComponentDescriptor component = descriptor.getComponent(featureName);
			if (component != null && component.getTypeDescriptor() != null) {
				SimpleTypeDescriptor componentType = (SimpleTypeDescriptor) component.getTypeDescriptor();
				Class<?> javaType = componentType.getPrimitiveType().getJavaType();
				if (stringSource)
					converter = ConverterManager.getInstance().createConverter(String.class, javaType);
				else
					converter = new AnyConverter(javaType);
			}
		}
		return converter;
	}

    public Entity convert(Object[] sourceValue) {
    	if (sourceValue == null)
    		return null;
        Entity entity = new Entity(descriptor);
        int length;
        if (sourceValue.length > featureNames.length) {
        	escalator.escalate("Row has more columns than specified in the file header", this, sourceValue);
        	length = featureNames.length;
        } else
        	length = sourceValue.length;
        for (int i = 0; i < length; i++)
			entityMutators[i].setValue(entity, sourceValue[i]);
        return entity;
    }
    
    @Override
    public String toString() {
    	return getClass().getSimpleName() + '[' + ArrayFormat.format(", ", featureNames) + ']';
    }
    
}

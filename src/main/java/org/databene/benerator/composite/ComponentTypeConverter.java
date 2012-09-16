/*
 * (c) Copyright 2008-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.composite;

import java.util.Map;

import org.databene.benerator.factory.DescriptorUtil;
import org.databene.commons.ConversionException;
import org.databene.commons.converter.AbstractConverter;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;

/**
 * Converts an Entity's components to the type specified by the EntityDescriptor.
 * This is used for e.g. importing Entities from file with String component values and 
 * converting them to the correct target type.<br/><br/>
 * Created at 06.05.2008 11:34:46
 * @since 0.5.3
 * @author Volker Bergmann
 */
public class ComponentTypeConverter extends AbstractConverter<Entity, Entity> {

	private ComplexTypeDescriptor type;

	public ComponentTypeConverter(ComplexTypeDescriptor type) {
		super(Entity.class, Entity.class);
		this.type = type;
	}

	public Entity convert(Entity entity) throws ConversionException {
		if (entity == null)
			return null;
		Map<String, Object> components = entity.getComponents();
		for (Map.Entry<String, Object> entry : components.entrySet()) {
			String componentName = entry.getKey();
			ComponentDescriptor componentDescriptor = type.getComponent(componentName);
			if (componentDescriptor != null) {
				TypeDescriptor componentType = componentDescriptor.getTypeDescriptor();
				Object componentValue = entry.getValue();
				if (componentType instanceof SimpleTypeDescriptor) {
					Object javaValue = DescriptorUtil.convertType(componentValue, (SimpleTypeDescriptor) componentType);
			        components.put(componentName, javaValue);
				} else {
			        components.put(componentName, convert((Entity) componentValue));
				}
			}
		}
		return entity;
	}

	public boolean isParallelizable() {
	    return false;
    }

	public boolean isThreadSafe() {
	    return false;
    }

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + type + "]";
	}
	
}

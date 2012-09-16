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

package org.databene.benerator.composite;

import org.databene.commons.ConversionException;
import org.databene.commons.converter.ThreadSafeConverter;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;

/**
 * Converts entities of arbitrary name to entities with a new name, 
 * keeping the attributes unchanged.<br/><br/>
 * Created: 09.07.2010 09:00:55
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class EntityTypeChanger extends ThreadSafeConverter<Entity, Entity> {
	
	private ComplexTypeDescriptor targetType;
	
	public EntityTypeChanger(ComplexTypeDescriptor targetType) {
	    super(Entity.class, Entity.class);
	    this.targetType = targetType;
    }

	public Entity convert(Entity entity) throws ConversionException {
		return changeType(entity, targetType);
    }

	public static Entity changeType(Entity entity, ComplexTypeDescriptor targetType) {
	    Entity result = new Entity(targetType);
	    result.setComponents(entity.getComponents());
	    return result;
	}
	
}

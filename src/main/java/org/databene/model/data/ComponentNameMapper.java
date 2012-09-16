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

package org.databene.model.data;

import java.util.Map;

import org.databene.benerator.primitive.ValueMapper;
import org.databene.commons.ConversionException;
import org.databene.commons.converter.ThreadSafeConverter;

/**
 * Converts the names of Entity components.<br/><br/>
 * Created: 22.02.2010 19:42:49
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ComponentNameMapper extends ThreadSafeConverter<Entity, Entity> {
	
	private ValueMapper nameMapper;

	public ComponentNameMapper() {
		this(null);
    }
	
	public ComponentNameMapper(String mappingSpec) {
		super(Entity.class, Entity.class);
	    this.nameMapper = new ValueMapper(mappingSpec, true);
    }
	
	public void setMappings(String mappingSpec) {
		nameMapper.setMappings(mappingSpec);
	}

	public Entity convert(Entity input) throws ConversionException {
		Entity output = new Entity(input.descriptor());
		for (Map.Entry<String, Object> component : input.getComponents().entrySet()) {
			String inCptName = component.getKey();
			String outCptName = (String) nameMapper.convert(inCptName);
			output.setComponent(outCptName, input.get(inCptName));
		}
	    return output;
    }

}

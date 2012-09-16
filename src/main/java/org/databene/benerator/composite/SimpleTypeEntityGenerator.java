/*
 * (c) Copyright 2008-2011 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.Generator;
import org.databene.benerator.wrapper.GeneratorWrapper;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;

/**
 * Generates Entities that wrap a content of simple type.<br/><br/>
 * Created at 11.05.2008 23:37:42
 * @since 0.5.4
 * @author Volker Bergmann
 */
public class SimpleTypeEntityGenerator extends GeneratorWrapper<Object, Entity> {
	
	private ComplexTypeDescriptor complexType;

	@SuppressWarnings("unchecked")
    public SimpleTypeEntityGenerator(Generator<?> source, ComplexTypeDescriptor complexType) {
		super((Generator<Object>) source);
		this.complexType = complexType;
	}

	// Generator interface implementation ------------------------------------------------------------------------------

	public Class<Entity> getGeneratedType() {
		return Entity.class;
	}
	
	public ProductWrapper<Entity> generate(ProductWrapper<Entity> wrapper) {
		ProductWrapper<Object> sourceWrapper = generateFromSource();
		if (sourceWrapper == null)
			return null;
		Object content = sourceWrapper.unwrap();
		Entity entity = new Entity(complexType);
		entity.setComponent(ComplexTypeDescriptor.__SIMPLE_CONTENT, content);
		return wrapper.wrap(entity);
	}

}

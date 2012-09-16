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

package org.databene.model.data;

import org.databene.commons.ConfigurationError;
import org.databene.commons.Mutator;
import org.databene.commons.StringUtil;
import org.databene.commons.UpdateFailedException;

/**
 * Builds and mutates graphs of entities.<br/><br/>
 * Created: 16.11.2011 18:14:46
 * @since 0.7.2
 * @author Volker Bergmann
 */
public class EntityGraphMutator implements Mutator {
	
	private String featureName;
	private ComplexTypeDescriptor descriptor;

	public EntityGraphMutator(String featureName, ComplexTypeDescriptor descriptor) {
		this.featureName = featureName;
		this.descriptor = descriptor;
	}

	public void setValue(Object target, Object value) throws UpdateFailedException {
		Entity entity = (Entity) target;
		setFeature(featureName, value, entity, descriptor);
	}

	private void setFeature(String featureName, Object value, Entity entity, ComplexTypeDescriptor descriptor) {
		if (featureName.contains(".")) {
			String[] subPaths = StringUtil.splitOnFirstSeparator(featureName, '.');
			ComponentDescriptor subComponent = descriptor.getComponent(subPaths[0]);
			if (subComponent == null)
				throw new ConfigurationError("Component '" + subPaths[0] + "' not found in type " + descriptor.getName());
			ComplexTypeDescriptor subType = (ComplexTypeDescriptor) subComponent.getTypeDescriptor();
			Entity subEntity = (Entity) entity.get(subPaths[0]);
			if (subEntity == null) {
				subEntity = new Entity(subType);
				entity.setComponent(subPaths[0], subEntity);
			}
			setFeature(subPaths[1], value, subEntity, subType);
		} else
			entity.setComponent(featureName, value);
	}

}

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

package org.databene.benerator.composite;

import org.databene.benerator.util.ThreadSafeGenerator;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.Assert;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;

/**
 * Instantiates an entity without initializing any components.<br/>
 * <br/>
 * Created: 01.09.2007 07:39:52
 * @author Volker Bergmann
 */
public class BlankEntityGenerator extends ThreadSafeGenerator<Entity> {

    private ComplexTypeDescriptor descriptor;

    public BlankEntityGenerator(ComplexTypeDescriptor descriptor) {
    	Assert.notNull(descriptor, "descriptor");
        this.descriptor = descriptor;
    }

	public Class<Entity> getGeneratedType() {
        return Entity.class;
    }

	public ProductWrapper<Entity> generate(ProductWrapper<Entity> wrapper) {
        return wrapper.wrap(new Entity(descriptor));
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + descriptor.getName() + "]";
	}
	
}

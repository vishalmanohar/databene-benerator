/*
 * (c) Copyright 2007-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.map;

import org.databene.commons.converter.ThreadSafeConverter;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;

import java.util.Map;
import java.util.Set;

/**
 * Converts a Map to an Entity.<br/>
 * <br/>
 * Created: 29.08.2007 18:44:41
 * @author Volker Bergmann
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Map2EntityConverter extends ThreadSafeConverter<Map, Entity> {

    private ComplexTypeDescriptor descriptor;

    public Map2EntityConverter(ComplexTypeDescriptor descriptor) {
    	super(Map.class, Entity.class);
        this.descriptor = descriptor;
    }

	public Entity convert(Map map) {
        Entity entity = new Entity(descriptor);
        for (Map.Entry entry : ((Set<Map.Entry>) map.entrySet()))
            entity.setComponent((String) entry.getKey(), entry.getValue());
        return entity;
    }

}

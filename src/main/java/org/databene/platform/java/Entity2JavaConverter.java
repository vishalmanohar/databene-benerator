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

package org.databene.platform.java;

import org.databene.model.data.Entity;
import org.databene.commons.BeanUtil;
import org.databene.commons.converter.ThreadSafeConverter;

import java.util.Map;

/**
 * Converts entities and entity arrays to Java beans and bean arrays.<br/>
 * <br/>
 * Created: 29.08.2007 08:50:24
 * @author Volker Bergmann
 */
public class Entity2JavaConverter extends ThreadSafeConverter<Object, Object> {

    public Entity2JavaConverter() {
        super(Object.class, Object.class);
    }

    public Object convert(Object entityOrArray) {
    	return convertAny(entityOrArray);
    }

    public static Object convertAny(Object entityOrArray) {
		if (entityOrArray == null)
    		return null;
    	else if (entityOrArray.getClass().isArray())
    		return convertArray((Object[]) entityOrArray);
    	else if (entityOrArray instanceof Entity)
    		return convertEntity((Entity) entityOrArray);
    	else
    		return entityOrArray;
	}

	private static Object convertArray(Object[] array) {
		Object[] result = new Object[array.length];
		for (int i = 0; i < array.length; i++)
			result[i] = convertAny(array[i]);
		return result;
	}

	private static Object convertEntity(Entity entity) {
		Object result = BeanUtil.newInstance(entity.type());
        for (Map.Entry<String, Object> entry : entity.getComponents().entrySet()) {
            Object value = convertAny(entry.getValue());
			BeanUtil.setPropertyValue(result, entry.getKey(), value, false);
        }
        return result;
	}

}

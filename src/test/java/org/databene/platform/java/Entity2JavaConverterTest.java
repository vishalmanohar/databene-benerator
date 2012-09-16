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

import java.util.Arrays;

import org.junit.Test;
import static junit.framework.Assert.*;

import org.databene.commons.ArrayFormat;
import org.databene.model.data.Entity;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.platform.PersonBean;
import org.databene.platform.java.Entity2JavaConverter;

/**
 * Tests the {@link Entity2JavaConverter}.<br/>
 * <br/>
 * Created: 29.08.2007 18:54:45
 * @author Volker Bergmann
 */
public class Entity2JavaConverterTest {

    final BeanDescriptorProvider provider = new BeanDescriptorProvider();

    @Test
    public void testEntity() {
		ComplexTypeDescriptor descriptor = getPersonTypeDescriptor();
        Entity entity = createAlice(descriptor);
        PersonBean bean = new PersonBean("Alice", 23);
        assertEquals(bean, new Entity2JavaConverter().convert(entity));
    }

	@Test
    public void testEntityArray() {
        ComplexTypeDescriptor descriptor = getPersonTypeDescriptor();
        Object[] expected = new Object[] { new PersonBean("Alice", 23), new PersonBean("Bob", 34) };
        Object[] array = new Object[] { createAlice(descriptor), createBob(descriptor) };
        Object[] actual = (Object[]) new Entity2JavaConverter().convert(array);
		assertTrue("Expected [" + ArrayFormat.format(expected) + "], found: [" + ArrayFormat.format(actual) + "]",
				Arrays.deepEquals(expected, actual));
    }

	private ComplexTypeDescriptor getPersonTypeDescriptor() {
		return (ComplexTypeDescriptor) provider.getTypeDescriptor(PersonBean.class.getName());
	}

	protected Entity createAlice(ComplexTypeDescriptor descriptor) {
		return new Entity(descriptor, "name", "Alice", "age", 23);
	}
	
	protected Entity createBob(ComplexTypeDescriptor descriptor) {
		return new Entity(descriptor, "name", "Bob", "age", 34);
	}
	
}

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

package org.databene.benerator.test;

import java.util.List;

import org.databene.commons.CollectionUtil;
import org.databene.model.data.AbstractEntitySource;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.DescriptorProvider;
import org.databene.model.data.Entity;
import org.databene.model.data.EntitySource;
import org.databene.model.data.PartDescriptor;
import org.databene.webdecs.DataIterator;
import org.databene.webdecs.util.DataIteratorFromJavaIterator;

/**
 * {@link EntitySource} implementation for testing.<br/>
 * <br/>
 * Created: 11.03.2010 12:42:48
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class PersonSource extends AbstractEntitySource {
	
	public DataIterator<Entity> iterator() {
		return new DataIteratorFromJavaIterator<Entity>(createPersons().iterator(), Entity.class);
	}

	public List<Entity> createPersons() {
		return CollectionUtil.toList(createAlice(), createBob(provider()));
	}

	public Entity createAlice() {
		return new Entity(createPersonDescriptor(), "name", "Alice", "age", "23");
	}

	public Entity createBob(DescriptorProvider provider) {
		return new Entity(createPersonDescriptor(), "name", "Bob", "age", "34");
	}
	
	public ComplexTypeDescriptor createPersonDescriptor() {
		return new ComplexTypeDescriptor(
				"Person", provider()).withComponent(new PartDescriptor("name", provider(), "string"))
				.withComponent(new PartDescriptor("age", provider(), "int"));
	}
	
	private DescriptorProvider provider() {
		return context.getLocalDescriptorProvider();
	}

}

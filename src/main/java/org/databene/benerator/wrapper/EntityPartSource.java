/*
 * (c) Copyright 2012 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.wrapper;

import java.util.Collection;
import java.util.Iterator;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.commons.CollectionUtil;
import org.databene.model.data.Entity;
import org.databene.model.data.EntitySource;
import org.databene.webdecs.DataContainer;
import org.databene.webdecs.DataIterator;
import org.databene.webdecs.util.AbstractDataIterator;
import org.databene.webdecs.util.AbstractDataSource;

/**
 * {@link EntitySource} implementation that looks up an entity by its name ({@link #productName}) in the context, 
 * accesses a 'part' component of it (by its {@link #partName}), and provides the entities contained within 
 * in the form of a {@link DataIterator}.<br/><br/>
 * Created: 06.03.2012 21:50:23
 * @since 0.7.6
 * @author Volker Bergmann
 */
public class EntityPartSource extends AbstractDataSource<Entity> implements EntitySource {
	
	protected String productName;
	protected String partName;
	protected BeneratorContext context;
	
	public EntityPartSource(String productName, String partName, BeneratorContext context) {
		super(Entity.class);
		this.productName = productName;
		this.partName = partName;
		this.context = context;
	}

	public DataIterator<Entity> iterator() {
		return new EntityPartIterator();
	}

	public class EntityPartIterator extends AbstractDataIterator<Entity> {
		
		private Iterator<Entity> source;
		
		@SuppressWarnings("unchecked")
		public EntityPartIterator() {
			Entity entity = (Entity) context.get(productName);
			Object part = entity.get(partName);
			if (part instanceof Collection)
				source = ((Collection<Entity>) part).iterator();
			else
				source = CollectionUtil.toList((Entity)part).iterator();
		}

		public DataContainer<Entity> next(DataContainer<Entity> container) {
			if (source.hasNext())
				return container.setData(source.next());
			else
				return null;
		}

	}

}

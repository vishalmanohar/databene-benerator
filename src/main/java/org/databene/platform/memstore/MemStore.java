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

package org.databene.platform.memstore;

import java.util.Collection;
import java.util.Map;

import org.databene.benerator.engine.expression.ScriptExpression;
import org.databene.benerator.storage.AbstractStorageSystem;
import org.databene.benerator.util.FilterExDataSource;
import org.databene.commons.CollectionUtil;
import org.databene.commons.Context;
import org.databene.commons.OrderedMap;
import org.databene.commons.StringUtil;
import org.databene.commons.collection.OrderedNameMap;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.Entity;
import org.databene.model.data.TypeDescriptor;
import org.databene.script.Expression;
import org.databene.script.ScriptUtil;
import org.databene.webdecs.DataSource;
import org.databene.webdecs.util.DataSourceFromIterable;
import org.databene.webdecs.util.DataSourceProxy;

/**
 * Simple heap-based implementation of the AbstractStorageSystem interface.<br/><br/>
 * Created: 07.03.2011 14:41:40
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class MemStore extends AbstractStorageSystem {

	static boolean ignoreClose = false; // for testing

	private final String id;
	private OrderedNameMap<ComplexTypeDescriptor> types;
	private Map<String, Map<Object, Entity>> typeMap;
	
	public MemStore(String id, DataModel dataModel) {
		this.setDataModel(dataModel);
		this.types = OrderedNameMap.createCaseInsensitiveMap();
		typeMap = OrderedNameMap.createCaseInsensitiveMap();
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public DataSource<Entity> queryEntities(String entityType, String selector, Context context) {
		Map<?, Entity> idMap = getOrCreateIdMapForType(entityType);
		DataSource<Entity> result = new DataSourceProxy<Entity>(new DataSourceFromIterable<Entity>(idMap.values(), Entity.class));
		if (!StringUtil.isEmpty(selector)) {
			Expression<Boolean> filterEx = new ScriptExpression<Boolean>(ScriptUtil.parseScriptText(selector));
			result = new FilterExDataSource<Entity>(result, filterEx , context);
		}
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DataSource<?> queryEntityIds(String entityType, String selector, Context context) {
		Map<?, Entity> idMap = getOrCreateIdMapForType(entityType);
		DataSource<?> result = new DataSourceProxy(new DataSourceFromIterable(idMap.keySet(), Object.class));
		if (!StringUtil.isEmpty(selector)) {
			Expression<Boolean> filterEx = new ScriptExpression<Boolean>(ScriptUtil.parseScriptText(selector));
			result = new FilterExDataSource(result, filterEx , context);
		}
		return result;
	}

	public DataSource<?> query(String selector, boolean simplify, Context context) {
		throw new UnsupportedOperationException(getClass() + " does not support query(String, Context)");
	}

	public void store(Entity entity) {
		String entityType = entity.type();
		Map<Object, Entity> idMap = getOrCreateIdMapForType(entityType);
		Object idComponentValues = entity.idComponentValues();
		if (idComponentValues == null)
			idComponentValues = entity.getComponents().values();
		idMap.put(idComponentValues, entity);
		if (!types.containsKey(entityType))
			types.put(entityType, new ComplexTypeDescriptor(entityType, this));
	}

	public void update(Entity entity) {
		store(entity);
	}

	public TypeDescriptor[] getTypeDescriptors() {
		return CollectionUtil.toArray(types.values(), TypeDescriptor.class);
	}

	public TypeDescriptor getTypeDescriptor(String typeName) {
		return types.get(typeName);
	}

	public void flush() {
	}

	public void close() {
		if (!ignoreClose)
			typeMap.clear();
	}
	
	public void printContent() {
		for (Map.Entry<String, Map<Object, Entity>> typeEntry : typeMap.entrySet()) {
			System.out.println(typeEntry.getKey() + ':');
			for (Map.Entry<Object, Entity> valueEntry : typeEntry.getValue().entrySet()) {
				System.out.println(valueEntry.getKey() + ": " + valueEntry.getValue());
			}
		}
	}

	private Map<Object, Entity> getOrCreateIdMapForType(String entityType) {
		Map<Object, Entity> idMap = typeMap.get(entityType);
		if (idMap == null) {
			idMap = new OrderedMap<Object, Entity>();
			typeMap.put(entityType, idMap);
		}
		return idMap;
	}

	public Collection<Entity> getEntities(String entityType) {
		return typeMap.get(entityType).values();
	}

}

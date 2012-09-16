/*
 * (c) Copyright 2008-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.model.data;

import java.util.Map;

import org.databene.commons.CollectionUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.collection.OrderedNameMap;
import org.databene.commons.xml.XMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the DescriptorProvider interface.<br/><br/>
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class DefaultDescriptorProvider implements DescriptorProvider {
    
    private static Logger logger = LoggerFactory.getLogger(DefaultDescriptorProvider.class);
    
    protected DataModel dataModel;
    protected Map<String, TypeDescriptor> typeMap;
    protected String id;
    private boolean redefinable;
    
    public DefaultDescriptorProvider(String id, DataModel dataModel) {
        this(id, dataModel, false);
    }

    public DefaultDescriptorProvider(String id, DataModel dataModel, boolean redefinable) {
        this.typeMap = new OrderedNameMap<TypeDescriptor>();
        this.id = id;
        this.redefinable = redefinable;
        if (dataModel != null)
        	dataModel.addDescriptorProvider(this);
    }

    public void addTypeDescriptor(TypeDescriptor descriptor) {
        if (!redefinable && typeMap.get(descriptor.getName()) != null)
            throw new ConfigurationError("Type has already been defined: " + descriptor.getName());
        typeMap.put(descriptor.getName(), descriptor);
        logger.debug("added " + descriptor.getClass().getSimpleName() + ": " + descriptor);
    }
    
    public DataModel getDataModel() {
    	return dataModel;
    }
    
	public void setDataModel(DataModel dataModel) {
		this.dataModel = dataModel;
	}
    
    public String getId() {
        return id;
    }

    public TypeDescriptor getTypeDescriptor(String typeName) {
        String localName = XMLUtil.localName(typeName);
        return typeMap.get(localName);
    }

    public TypeDescriptor[] getTypeDescriptors() {
        return CollectionUtil.toArray(typeMap.values(), TypeDescriptor.class);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + id + ')';
    }
}

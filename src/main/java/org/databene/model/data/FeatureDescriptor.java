/*
 * (c) Copyright 2008-2012 by Volker Bergmann. All rights reserved.
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

import org.databene.commons.Named;
import org.databene.commons.NullSafeComparator;
import org.databene.commons.Operation;
import org.databene.commons.StringUtil;
import org.databene.commons.collection.OrderedNameMap;
import org.databene.commons.converter.AnyConverter;
import org.databene.commons.converter.ConverterManager;
import org.databene.commons.converter.String2ConverterConverter;
import org.databene.commons.converter.ToStringConverter;

import java.util.List;

/**
 * Common parent class of all descriptors.<br/><br/>
 * Created: 17.07.2006 21:30:45
 * @since 0.1
 * @author Volker Bergmann
 */
public class FeatureDescriptor implements Named {

    public static final String NAME = "name";

    static {
        ConverterManager.getInstance().registerConverterClass(String2ConverterConverter.class);
    }

    protected OrderedNameMap<FeatureDetail<?>> details;
    protected DescriptorProvider provider;

    // constructor -----------------------------------------------------------------------------------------------------

    public FeatureDescriptor(String name, DescriptorProvider provider) {
    	if (provider == null)
    		throw new IllegalArgumentException("provider is null");
    	if (provider.getDataModel() == null)
    		throw new IllegalArgumentException("provider's data model is null");
        this.details = new OrderedNameMap<FeatureDetail<?>>();
        this.provider = provider;
        this.addConstraint(NAME, String.class, null);
        this.setName(name);
    }
    
    // typed interface -------------------------------------------------------------------------------------------------
/*
    public void setParent(FeatureDescriptor parent) {
        this.parent = parent;
    }
*/
    public String getName() {
        return (String) getDetailValue(NAME);
    }

    public void setName(String name) {
        setDetailValue(NAME, name);
    }
    
    public DescriptorProvider getProvider() {
    	return provider;
    }
    
    public DataModel getDataModel() {
    	return provider.getDataModel();
    }

    // generic detail access -------------------------------------------------------------------------------------------

    public boolean supportsDetail(String name) {
        return details.containsKey(name);
    }

    public Object getDeclaredDetailValue(String name) { // TODO v0.8 remove method? It does not differ from getDetailValue any more
        return getConfiguredDetail(name).getValue();
    }

    public Object getDetailValue(String name) { // TODO v0.8 remove generic feature access?
        return this.getConfiguredDetail(name).getValue();
    }

    public void setDetailValue(String detailName, Object detailValue) {
        FeatureDetail<Object> detail = getConfiguredDetail(detailName);
        detail.setValue(AnyConverter.convert(detailValue, detail.getType()));
    }

    public List<FeatureDetail<?>> getDetails() {
        return details.values();
    }

    // java.lang overrides ---------------------------------------------------------------------------------------------

    @Override
	public String toString() {
        String name = getName();
        if (StringUtil.isEmpty(name))
            name = "anonymous";
        return renderDetails(new StringBuilder(name)).toString();
    }

    @Override
	public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final FeatureDescriptor that = (FeatureDescriptor) o;
        for (FeatureDetail<?> detail : details.values()) {
            String detailName = detail.getName();
            if (!NullSafeComparator.equals(detail.getValue(), that.getDetailValue(detailName)))
                return false;
        }
        return true;
    }

    @Override
	public int hashCode() {
        return (getClass().hashCode() * 29 /*+ (parent != null ? parent.hashCode() : 0)*/) * 29 + details.hashCode();
    }

    // helpers ---------------------------------------------------------------------------------------------------------

	protected String renderDetails() {
		return renderDetails(new StringBuilder()).toString();
	}
	
	protected StringBuilder renderDetails(StringBuilder builder) {
		builder.append("[");
        boolean empty = true;
        for (FeatureDetail<?> descriptor : details.values())
            if (descriptor.getValue() != null && !NAME.equals(descriptor.getName())) {
                if (!empty)
                    builder.append(", ");
                empty = false;
                builder.append(descriptor.getName()).append("=");
                builder.append(ToStringConverter.convert(descriptor.getValue(), "[null]"));
            }
        return builder.append("]");
	}

    protected Class<?> getDetailType(String detailName) {
        FeatureDetail<?> detail = details.get(detailName);
        if (detail == null)
            throw new UnsupportedOperationException("Feature detail not supported: " + detailName);
        return detail.getType();
    }

    protected <T> void addConfig(String name, Class<T> type) {
    	addConfig(name, type, false);
    }

    protected <T> void addConfig(String name, Class<T> type, boolean deprecated) {
        addDetail(name, type, false, deprecated, null);
    }

    protected <T> void addConstraint(String name, Class<T> type, Operation<T, T> combinator) {
        addDetail(name, type, true, false, combinator);
    }

    protected <T> void addDetail(String detailName, Class<T> detailType, boolean constraint, 
    		boolean deprecated, Operation<T,T> combinator) {
        this.details.put(detailName, new FeatureDetail<T>(detailName, detailType, constraint, combinator));
    }

    // generic property access -----------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public <T> FeatureDetail<T> getConfiguredDetail(String name) {
    	if (!supportsDetail(name))
            throw new UnsupportedOperationException("Feature detail '" + name + 
            		"' not supported in feature type: " + getClass().getName());
        return (FeatureDetail<T>) details.get(name);
    }
    
}

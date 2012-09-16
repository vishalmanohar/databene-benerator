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

package org.databene.benerator.engine;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.databene.benerator.factory.DefaultsProvider;
import org.databene.benerator.factory.GeneratorFactory;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.Context;
import org.databene.commons.context.CaseInsensitiveContext;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.DescriptorProvider;
import org.databene.model.data.TypeDescriptor;

/**
 * Sub context version of the {@link BeneratorContext}.<br/><br/>
 * Created: 02.09.2011 14:35:59
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class BeneratorSubContext implements BeneratorContext {
	
	private BeneratorContext parent;
	private Context localContext = new CaseInsensitiveContext(true);
	private ProductWrapper<?> currentProduct;
	private String currentProductName;

	public BeneratorSubContext(BeneratorContext parent) {
		this.parent = parent;
		this.currentProductName = null;
	}
	
	public BeneratorContext getParent() {
		return parent;
	}

	public String getDefaultEncoding() {
		return parent.getDefaultEncoding();
	}

	public Object get(String key) {
		if (localContext.contains(key))
			return localContext.get(key);
		else
			return parent.get(key);
	}
	
	public void set(String key, Object value) {
		localContext.set(key, value);
	}

	public ProductWrapper<?> getCurrentProduct() {
		return currentProduct;
	}

	public void setCurrentProduct(ProductWrapper<?> currentProduct) {
		this.currentProduct = currentProduct;
		if (currentProduct != null)
			set("this", currentProduct.unwrap());
		else
			remove("this");
	}
	
	public Set<String> keySet() {
        Set<String> keySet = new HashSet<String>(parent.keySet());
        keySet.addAll(localContext.keySet());
        return keySet;
	}

	public Set<Entry<String, Object>> entrySet() {
		Set<Entry<String, Object>> entrySet = new HashSet<Entry<String,Object>>(parent.entrySet());
		entrySet.addAll(localContext.entrySet());
		return entrySet;
	}


	// simple delegates ------------------------------------------------------------------------------------------------
	
	public String getDefaultLineSeparator() {
		return parent.getDefaultLineSeparator();
	}

	public Locale getDefaultLocale() {
		return parent.getDefaultLocale();
	}

	public void remove(String key) {
		parent.remove(key);
	}

	public String getDefaultDataset() {
		return parent.getDefaultDataset();
	}

	public long getDefaultPageSize() {
		return parent.getDefaultPageSize();
	}

	public String getDefaultScript() {
		return parent.getDefaultScript();
	}

	public boolean isDefaultNull() {
		return parent.isDefaultNull();
	}

	public boolean contains(String key) {
		return localContext.contains(key) || parent.contains(key);
	}

	public char getDefaultSeparator() {
		return parent.getDefaultSeparator();
	}

	public String getDefaultErrorHandler() {
		return parent.getDefaultErrorHandler();
	}

	public String getContextUri() {
		return parent.getContextUri();
	}

	public boolean isValidate() {
		return parent.isValidate();
	}

	public Long getMaxCount() {
		return parent.getMaxCount();
	}

	public BeneratorContext createSubContext() {
		return new BeneratorSubContext(this);
	}

	public GeneratorFactory getGeneratorFactory() {
		return parent.getGeneratorFactory();
	}

	public void setGeneratorFactory(GeneratorFactory generatorFactory) {
		parent.setGeneratorFactory(generatorFactory);
	}

	public Object getSetting(String name) {
		return parent.getSetting(name);
	}

	public DefaultsProvider getDefaultsProvider() {
		return parent.getDefaultsProvider();
	}

	public void setDefaultsProvider(DefaultsProvider defaultsProvider) {
		parent.setDefaultsProvider(defaultsProvider);
	}

	public Class<?> forName(String className) {
		return parent.forName(className);
	}

	public ExecutorService getExecutorService() {
		return parent.getExecutorService();
	}

	public void setSetting(String name, Object value) {
		parent.setSetting(name, value);
	}

	public String resolveRelativeUri(String relativeUri) {
		return parent.resolveRelativeUri(relativeUri);
	}

	public void close() {
		parent.close();
	}

	public void importClass(String className) {
		parent.importClass(className);
	}

	public void importPackage(String packageName) {
		parent.importPackage(packageName);
	}

	public void importDefaults() {
		parent.importDefaults();
	}

	public void setDefaultEncoding(String defaultEncoding) {
		parent.setDefaultEncoding(defaultEncoding);
	}

	public void setDefaultLineSeparator(String defaultLineSeparator) {
		parent.setDefaultLineSeparator(defaultLineSeparator);
	}

	public void setDefaultLocale(Locale defaultLocale) {
		parent.setDefaultLocale(defaultLocale);
	}

	public void setDefaultDataset(String defaultDataset) {
		parent.setDefaultDataset(defaultDataset);
	}

	public void setDefaultPageSize(long defaultPageSize) {
		parent.setDefaultPageSize(defaultPageSize);
	}

	public void setDefaultScript(String defaultScript) {
		parent.setDefaultScript(defaultScript);
	}

	public void setDefaultNull(boolean defaultNull) {
		parent.setDefaultNull(defaultNull);
	}

	public void setDefaultSeparator(char defaultSeparator) {
		parent.setDefaultSeparator(defaultSeparator);
	}

	public ComponentDescriptor getDefaultComponentConfig(String name) {
		return parent.getDefaultComponentConfig(name);
	}

	public void setDefaultComponentConfig(ComponentDescriptor component) {
		parent.setDefaultComponentConfig(component);
	}

	public void setDefaultErrorHandler(String defaultErrorHandler) {
		parent.setDefaultErrorHandler(defaultErrorHandler);
	}

	public void setContextUri(String contextUri) {
		parent.setContextUri(contextUri);
	}

	public void setValidate(boolean validate) {
		parent.setValidate(validate);
	}

	public void setMaxCount(Long maxCount) {
		parent.setMaxCount(maxCount);
	}

	public void setExecutorService(ExecutorService executorService) {
		parent.setExecutorService(executorService);
	}

	public boolean isDefaultOneToOne() {
		return parent.isDefaultOneToOne();
	}

	public void setDefaultOneToOne(boolean defaultOneToOne) {
		parent.setDefaultOneToOne(defaultOneToOne);
	}

	public boolean isAcceptUnknownSimpleTypes() {
		return parent.isAcceptUnknownSimpleTypes();
	}

	public void setAcceptUnknownSimpleTypes(boolean acceptUnknownSimpleTypes) {
		parent.setAcceptUnknownSimpleTypes(acceptUnknownSimpleTypes);
	}

	public boolean isDefaultImports() {
		return parent.isDefaultImports();
	}

	public void setDefaultImports(boolean defaultImports) {
		parent.setDefaultImports(defaultImports);
	}

	public DataModel getDataModel() {
		return parent.getDataModel();
	}

	public void setDataModel(DataModel dataModel) {
		parent.setDataModel(dataModel);
	}

	public DescriptorProvider getLocalDescriptorProvider() {
		return parent.getLocalDescriptorProvider();
	}
	
	public void addLocalType(TypeDescriptor type) {
		parent.addLocalType(type);
	}

	public void setCurrentProductName(String currentProductName) {
		this.currentProductName = currentProductName;
	}

	public boolean hasProductNameInScope(String currentProductName) {
		return (this.currentProductName != null && this.currentProductName.equals(currentProductName))
			|| (parent != null && parent.hasProductNameInScope(currentProductName));
	}

}

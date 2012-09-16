/*
 * (c) Copyright 2008-2011 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General License.
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

package org.databene.benerator.engine;

import java.util.Locale;
import java.util.concurrent.ExecutorService;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.factory.DefaultsProvider;
import org.databene.benerator.factory.GeneratorFactory;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.DescriptorProvider;
import org.databene.model.data.TypeDescriptor;
import org.databene.script.ScriptContext;

/**
 * A BeneratorContext.<br/><br/>
 * Created at 20.04.2008 06:41:04
 * @since 0.5.2
 * @author Volker Bergmann
 */
public interface BeneratorContext extends GeneratorContext, ScriptContext {

	BeneratorContext createSubContext();
	
	GeneratorFactory getGeneratorFactory();
	void setGeneratorFactory(GeneratorFactory generatorFactory);
	DataModel getDataModel();
	void setDataModel(DataModel dataModel);
	DefaultsProvider getDefaultsProvider();
	void setDefaultsProvider(DefaultsProvider defaultsProvider);
	void setSetting(String name, Object value);

	void setDefaultEncoding(String defaultEncoding);
	void setDefaultLineSeparator(String defaultLineSeparator);
	void setDefaultLocale(Locale defaultLocale);
	void setDefaultDataset(String defaultDataset);
    void setDefaultPageSize(long defaultPageSize);
    void setDefaultScript(String defaultScript);
    void setDefaultNull(boolean defaultNull);
	void setDefaultSeparator(char defaultSeparator);
	void setDefaultErrorHandler(String defaultErrorHandler);
	void setContextUri(String contextUri);
	void setValidate(boolean validate);
	void setMaxCount(Long maxCount);
	void setExecutorService(ExecutorService executorService);
	ProductWrapper<?> getCurrentProduct();
	void setCurrentProduct(ProductWrapper<?> currentProduct);
	
	boolean isDefaultImports();
	void setDefaultImports(boolean defaultImports);
	void importClass(String className);
	void importPackage(String packageName);
	void importDefaults();

	boolean isDefaultOneToOne();
	void setDefaultOneToOne(boolean defaultOneToOne);

	boolean isAcceptUnknownSimpleTypes();
	void setAcceptUnknownSimpleTypes(boolean acceptUnknownSimpleTypes);

	ComponentDescriptor getDefaultComponentConfig(String name);
	void setDefaultComponentConfig(ComponentDescriptor component);

	DescriptorProvider getLocalDescriptorProvider();
	void addLocalType(TypeDescriptor type);

	void setCurrentProductName(String currentProductName);
	boolean hasProductNameInScope(String currentProductName);
}

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

import java.io.File;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.databene.benerator.engine.parser.String2DistributionConverter;
import org.databene.benerator.factory.DefaultsProvider;
import org.databene.benerator.factory.GeneratorFactory;
import org.databene.benerator.factory.StochasticGeneratorFactory;
import org.databene.benerator.script.BeneratorScriptFactory;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.ErrorHandler;
import org.databene.commons.IOUtil;
import org.databene.commons.Level;
import org.databene.commons.LocaleUtil;
import org.databene.commons.SystemInfo;
import org.databene.commons.bean.ClassCache;
import org.databene.commons.context.CaseInsensitiveContext;
import org.databene.commons.context.ContextStack;
import org.databene.commons.context.DefaultContext;
import org.databene.commons.converter.ConverterManager;
import org.databene.commons.file.FileSuffixFilter;
import org.databene.domain.address.Country;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.DefaultDescriptorProvider;
import org.databene.model.data.DescriptorProvider;
import org.databene.model.data.TypeDescriptor;
import org.databene.script.ScriptUtil;

/**
 * Default implementation of {@link BeneratorContext}.<br/><br/>
 * Created: 02.09.2011 14:36:58
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class DefaultBeneratorContext extends ContextStack implements BeneratorContext {

    public static final String CELL_SEPARATOR_SYSPROP = "cell.separator";
 	public static final char DEFAULT_CELL_SEPARATOR = ',';

	private GeneratorFactory generatorFactory;
    private DefaultContext settings;
	private ClassCache classCache;
	
    protected String  defaultEncoding      = SystemInfo.getFileEncoding();
    protected String  defaultDataset       = LocaleUtil.getDefaultCountryCode();
    protected long    defaultPageSize      = 1;
    protected boolean defaultNull          = true;
    protected String  contextUri           = "./";
    public    Long    maxCount             = null;
    public    boolean defaultOneToOne      = false;
    public    boolean defaultImports       = true;
    public    boolean acceptUnknownSimpleTypes = false;


    protected ComplexTypeDescriptor defaultComponent;
    protected ExecutorService executorService = Executors.newCachedThreadPool();

	private ProductWrapper<?> currentProduct;

	private DataModel dataModel;
	private DefaultDescriptorProvider localDescriptorProvider;
	
	private String currentCreator;

    static {
    	ScriptUtil.addFactory("ben", new BeneratorScriptFactory());
    	ScriptUtil.setDefaultScriptEngine("ben");
    	ConverterManager.getInstance().registerConverterClass(String2DistributionConverter.class);
    }
    
	public DefaultBeneratorContext() {
		this(".");
	}
	
	public DefaultBeneratorContext(String contextUri) {
		if (contextUri == null)
			throw new ConfigurationError("No context URI specified");
		this.contextUri = contextUri;
		this.dataModel = new DataModel();
		this.localDescriptorProvider = new DefaultDescriptorProvider("ctx", dataModel);
		this.defaultComponent = new ComplexTypeDescriptor("benerator:defaultComponent", localDescriptorProvider);
		this.generatorFactory = new StochasticGeneratorFactory();
		settings = new DefaultContext();
		push(new DefaultContext(java.lang.System.getenv()));
		push(new DefaultContext(java.lang.System.getProperties()));
		push(settings);
		push(new CaseInsensitiveContext(true));
		set("context", this);
		if (IOUtil.isFileUri(contextUri))
			addLibFolderToClassLoader();
		classCache = new ClassCache();
	}
	
	public BeneratorContext createSubContext() {
		return new BeneratorSubContext(this);
	}
	
	// interface -------------------------------------------------------------------------------------------------------
	
	public GeneratorFactory getGeneratorFactory() {
		return generatorFactory;
	}

	public void setGeneratorFactory(GeneratorFactory generatorFactory) {
		this.generatorFactory = generatorFactory;
	}
	
	public DescriptorProvider getLocalDescriptorProvider() {
		return localDescriptorProvider;
	}
	
	public void setDefaultsProvider(DefaultsProvider defaultsProvider) {
		this.generatorFactory.setDefaultsProvider(defaultsProvider);
	}
	
	public void addLocalType(TypeDescriptor type) {
		localDescriptorProvider.addTypeDescriptor(type);
	}

	public void setSetting(String name, Object value) {
		settings.set(name, value);
	}
	
	public Object getSetting(String name) {
		return settings.get(name);
	}
	
	public void close() {
		executorService.shutdownNow();
	}
	
    public Class<?> forName(String className) {
		return classCache.forName(className);
	}
	
	public void importClass(String className) {
		classCache.importClass(className);
	}

	public void importPackage(String packageName) {
		classCache.importPackage(packageName);
	}

	public void importDefaults() {
		// import frequently used Benerator packages
		importPackage("org.databene.benerator.consumer");
		importPackage("org.databene.benerator.primitive");
		importPackage("org.databene.benerator.primitive.datetime");
		importPackage("org.databene.benerator.distribution.sequence");
		importPackage("org.databene.benerator.distribution.function");
		importPackage("org.databene.benerator.distribution.cumulative");
		importPackage("org.databene.benerator.sample");
		// import ConsoleExporter and LoggingConsumer
		importPackage("org.databene.model.consumer");
		// import formats, converters and validators from commons
		importPackage("org.databene.commons.converter");
		importPackage("org.databene.commons.format");
		importPackage("org.databene.commons.validator");
		// import standard platforms
		importPackage("org.databene.platform.fixedwidth");
		importPackage("org.databene.platform.csv");
		importPackage("org.databene.platform.dbunit");
		importPackage("org.databene.platform.xls");
	}

	// properties ------------------------------------------------------------------------------------------------------
	
    public String getDefaultEncoding() {
        return defaultEncoding;
    }
    
    public void setDefaultEncoding(String defaultEncoding) {
    	SystemInfo.setFileEncoding(defaultEncoding);
        this.defaultEncoding = defaultEncoding;
    }
    
    public String getDefaultLineSeparator() {
		return SystemInfo.getLineSeparator();
	}

	public void setDefaultLineSeparator(String defaultLineSeparator) {
    	SystemInfo.setLineSeparator(defaultLineSeparator);
	}

	public Locale getDefaultLocale() {
		return Locale.getDefault();
	}

	public void setDefaultLocale(Locale defaultLocale) {
		Locale.setDefault(defaultLocale);
	}

	public String getDefaultDataset() {
		return defaultDataset;
	}

	public void setDefaultDataset(String defaultDataset) {
		this.defaultDataset = defaultDataset;
		Country country = Country.getInstance(defaultDataset, false);
		if (country != null)
			Country.setDefault(country);
	}

	public long getDefaultPageSize() {
        return defaultPageSize;
    }
    
    public void setDefaultPageSize(long defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }
    
    public String getDefaultScript() {
        return ScriptUtil.getDefaultScriptEngine();
    }
    
    public void setDefaultScript(String defaultScript) {
        ScriptUtil.setDefaultScriptEngine(defaultScript);
    }
    
    public boolean isDefaultNull() {
        return defaultNull;
    }
    
    public void setDefaultNull(boolean defaultNull) {
        this.defaultNull = defaultNull;
    }
    
	public char getDefaultSeparator() {
		return getDefaultCellSeparator();
	}

	public void setDefaultSeparator(char defaultSeparator) {
		System.setProperty(CELL_SEPARATOR_SYSPROP, String.valueOf(defaultSeparator));
	}

	public ComponentDescriptor getDefaultComponentConfig(String name) {
		return defaultComponent.getComponent(name);
	}

	public void setDefaultComponentConfig(ComponentDescriptor component) {
		defaultComponent.addComponent(component);
	}

	public String getDefaultErrorHandler() {
		return ErrorHandler.getDefaultLevel().name();
	}

	public void setDefaultErrorHandler(String defaultErrorHandler) {
		ErrorHandler.setDefaultLevel(Level.valueOf(defaultErrorHandler));
	}

	public String getContextUri() {
		return contextUri;
	}

	public void setContextUri(String contextUri) {
		this.contextUri = contextUri;
	}

	public boolean isValidate() {
		return BeneratorOpts.isValidating();
	}

	public void setValidate(boolean validate) {
		BeneratorOpts.setValidating(validate);
	}
	
	public Long getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(Long maxCount) {
		this.maxCount = maxCount;
	}
	
	public ExecutorService getExecutorService() {
    	return executorService;
    }

	public void setExecutorService(ExecutorService executorService) {
    	this.executorService = executorService;
    }
	
	public String resolveRelativeUri(String relativeUri) {
	    return IOUtil.resolveRelativeUri(relativeUri, contextUri);
    }

	public boolean isDefaultOneToOne() {
    	return defaultOneToOne;
    }

	public void setDefaultOneToOne(boolean defaultOneToOne) {
    	this.defaultOneToOne = defaultOneToOne;
    }

	public boolean isAcceptUnknownSimpleTypes() {
    	return acceptUnknownSimpleTypes;
    }

	public void setAcceptUnknownSimpleTypes(boolean acceptUnknownSimpleTypes) {
    	this.acceptUnknownSimpleTypes = acceptUnknownSimpleTypes;
    	dataModel.setAcceptUnknownPrimitives(acceptUnknownSimpleTypes);
    }
    
	public static char getDefaultCellSeparator() {
		String tmp = System.getProperty(CELL_SEPARATOR_SYSPROP);
		if (tmp == null)
			return DEFAULT_CELL_SEPARATOR;
		if (tmp.length() != 1)
			throw new ConfigurationError("Cell separator has illegal length: '" + tmp + "'");
		return tmp.charAt(0);
	}

	public DefaultsProvider getDefaultsProvider() {
		return getGeneratorFactory().getDefaultsProvider();
	}

	public void setDefaultImports(boolean defaultImports) {
		this.defaultImports = defaultImports;
	}
	
	public boolean isDefaultImports() {
		return defaultImports;
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

	public DataModel getDataModel() {
		return dataModel;
	}

	public void setDataModel(DataModel dataModel) {
		this.dataModel = dataModel;
	}
	
	private void addLibFolderToClassLoader() {
		File libFolder = new File(contextUri, "lib");
		if (libFolder.exists()) {
			Thread.currentThread().setContextClassLoader(BeanUtil.createDirectoryClassLoader(libFolder));
			for (File jarFile : libFolder.listFiles(new FileSuffixFilter("jar", false)))
				Thread.currentThread().setContextClassLoader(BeanUtil.createJarClassLoader(jarFile));
		}
	}

	public void setCurrentProductName(String currentCreator) {
		this.currentCreator = currentCreator;
	}

	public boolean hasProductNameInScope(String currentCreator) {
		return (this.currentCreator != null && this.currentCreator.equals(currentCreator));
	}

}

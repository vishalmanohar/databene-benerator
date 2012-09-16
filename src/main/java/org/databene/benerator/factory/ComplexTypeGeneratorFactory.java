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

package org.databene.benerator.factory;

import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.EntitySource;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.Mode;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.data.Uniqueness;
import org.databene.benerator.*;
import org.databene.benerator.composite.BlankEntityGenerator;
import org.databene.benerator.composite.ComponentTypeConverter;
import org.databene.benerator.composite.GeneratorComponent;
import org.databene.benerator.composite.SimpleTypeEntityGenerator;
import org.databene.benerator.composite.SourceAwareGenerator;
import org.databene.benerator.distribution.DistributingGenerator;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.expression.ScriptExpression;
import org.databene.benerator.util.FilteringGenerator;
import org.databene.benerator.wrapper.*;
import org.databene.commons.*;
import org.databene.document.fixedwidth.FixedWidthColumnDescriptor;
import org.databene.document.fixedwidth.FixedWidthUtil;
import org.databene.platform.dbunit.DbUnitEntitySource;
import org.databene.platform.fixedwidth.FixedWidthEntitySource;
import org.databene.platform.xls.XLSEntitySourceProvider;
import org.databene.platform.csv.CSVEntitySourceProvider;
import org.databene.script.BeanSpec;
import org.databene.script.DatabeneScriptParser;
import org.databene.script.Expression;
import org.databene.script.ScriptConverterForStrings;
import org.databene.script.ScriptUtil;
import org.databene.webdecs.DataSource;

import java.util.*;

/**
 * Creates entity generators from entity metadata.<br/>
 * <br/>
 * Created: 08.09.2007 07:45:40
 * @author Volker Bergmann
 */
public class ComplexTypeGeneratorFactory extends TypeGeneratorFactory<ComplexTypeDescriptor> {

    public ComplexTypeGeneratorFactory() { }
    
	@Override
	protected Generator<?> applyComponentBuilders(Generator<?> generator, ComplexTypeDescriptor descriptor, 
			String instanceName, Uniqueness uniqueness, BeneratorContext context) {
		generator = createMutatingEntityGenerator(instanceName, descriptor, uniqueness, context, generator);
        return super.applyComponentBuilders(generator, descriptor, instanceName, uniqueness, context);
	}
    
    @Override
    protected Generator<Entity> createSourceGenerator(
    		ComplexTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
        // if no sourceObject is specified, there's nothing to do
        String sourceSpec = descriptor.getSource();
        if (sourceSpec == null)
            return null;
        Object sourceObject = null;
        if (ScriptUtil.isScript(sourceSpec)) {
        	Object tmp = ScriptUtil.evaluate(sourceSpec, context); // TODO v0.8 When to resolve scripts?
        	if (tmp != null && tmp instanceof String) {
        		sourceSpec = (String) tmp;
        		sourceObject = context.get(sourceSpec);
        	} else
        		sourceObject = tmp;
        } else if (context.hasProductNameInScope(sourceSpec)) {
        	String partName = StringUtil.lastToken(descriptor.getName(), '.');
			sourceObject = new EntityPartSource(sourceSpec, partName, context);
        } else {
    		sourceObject = context.get(sourceSpec);
        }

        // create sourceObject generator
        
        Generator<Entity> generator = null;
        if (sourceObject != null)
            generator = createSourceGeneratorFromObject(descriptor, context, sourceObject);
        else {
        	String lcSourceName = sourceSpec.toLowerCase();
        	if (lcSourceName.endsWith(".xml"))
	            generator = new DataSourceGenerator<Entity>(new DbUnitEntitySource(sourceSpec, context));
	        else if (lcSourceName.endsWith(".csv"))
	            generator = createCSVSourceGenerator(descriptor, context, sourceSpec);
	        else if (lcSourceName.endsWith(".fcw"))
	            generator = createFixedColumnWidthSourceGenerator(descriptor, context, sourceSpec);
	        else if (lcSourceName.endsWith(".xls"))
	            generator = createXLSSourceGenerator(descriptor, context, sourceSpec);
	        else {
	        	try {
		        	BeanSpec sourceBeanSpec = DatabeneScriptParser.resolveBeanSpec(sourceSpec, context);
		        	sourceObject = sourceBeanSpec.getBean();
		        	generator = createSourceGeneratorFromObject(descriptor, context, sourceObject);
		        	if (sourceBeanSpec.isReference() && !(sourceObject instanceof StorageSystem))
		        		generator = WrapperFactory.preventClosing(generator);
	        	} catch (Exception e) {
	        		throw new UnsupportedOperationException("Error resolving source: " + sourceSpec, e);
	        	}
	        }
        }
        if (generator.getGeneratedType() != Entity.class)
        	generator = new SimpleTypeEntityGenerator(generator, descriptor);
        if (descriptor.getFilter() != null) {
        	Expression<Boolean> filter 
        		= new ScriptExpression<Boolean>(ScriptUtil.parseScriptText(descriptor.getFilter()));
        	generator = new FilteringGenerator<Entity>(generator, filter);
        }
    	Distribution distribution = FactoryUtil.getDistribution(descriptor.getDistribution(), uniqueness, false, context);
        if (distribution != null)
        	generator = new DistributingGenerator<Entity>(generator, distribution, uniqueness.isUnique());
    	return generator;
    }

	@Override
	protected Generator<?> createSpecificGenerator(ComplexTypeDescriptor descriptor, String instanceName,
			boolean nullable, Uniqueness uniqueness, BeneratorContext context) {
		return null;
	}

	@Override
	protected Generator<?> createHeuristicGenerator(ComplexTypeDescriptor type, String instanceName, 
			Uniqueness uniqueness, BeneratorContext context) {
		if (DescriptorUtil.isWrappedSimpleType(type))
    		return createSimpleTypeEntityGenerator(type, uniqueness, context);
        else
    		return new BlankEntityGenerator(type);
	}

	@Override
	protected Class<?> getGeneratedType(ComplexTypeDescriptor descriptor) {
		return Entity.class;
	}

    @SuppressWarnings("unchecked")
	public static Generator<?> createMutatingEntityGenerator(String name, ComplexTypeDescriptor descriptor, 
    		Uniqueness ownerUniqueness, BeneratorContext context, Generator<?> source) {
    	List<GeneratorComponent<Entity>> generatorComponent = 
    		createMutatingGeneratorComponents(descriptor, ownerUniqueness, context);
        return new SourceAwareGenerator<Entity>(name, (Generator<Entity>) source, generatorComponent, context);
    }

    // private helpers -------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private static Generator<Entity> createSourceGeneratorFromObject(ComplexTypeDescriptor descriptor,
            BeneratorContext context, Object sourceObject) {
    	Generator<Entity> generator;
	    if (sourceObject instanceof StorageSystem) {
	        StorageSystem storage = (StorageSystem) sourceObject;
	        String selector = descriptor.getSelector();
	        String subSelector = descriptor.getSubSelector();
	        if (!StringUtil.isEmpty(subSelector)) {
				DataSource<Entity> dataSource = storage.queryEntities(descriptor.getName(), subSelector, context);
				generator = WrapperFactory.applyHeadCycler(new DataSourceGenerator<Entity>(dataSource));
			} else 
	        	generator = new DataSourceGenerator<Entity>(storage.queryEntities(descriptor.getName(), selector, context));
	    } else if (sourceObject instanceof EntitySource) {
	        generator = new DataSourceGenerator<Entity>((EntitySource) sourceObject);
	    } else if (sourceObject instanceof Generator) {
	        generator = (Generator<Entity>) sourceObject;
	    } else
	        throw new UnsupportedOperationException("Source type not supported: " + sourceObject.getClass());
	    return generator;
    }

	private static Generator<Entity> createFixedColumnWidthSourceGenerator(
			ComplexTypeDescriptor descriptor, BeneratorContext context, String sourceName) {
		Generator<Entity> generator;
		String encoding = descriptor.getEncoding();
		if (encoding == null)
		    encoding = context.getDefaultEncoding();
		String pattern = descriptor.getPattern();
		if (pattern == null)
		    throw new ConfigurationError("No pattern specified for FCW file import: " + sourceName);
		FixedWidthColumnDescriptor[] ffcd = FixedWidthUtil.parseColumnsSpec(pattern);
		Converter<String, String> scriptConverter = DescriptorUtil.createStringScriptConverter(context);
		FixedWidthEntitySource iterable = new FixedWidthEntitySource(sourceName, descriptor, scriptConverter, encoding, null, ffcd);
		iterable.setContext(context);
		generator = new DataSourceGenerator<Entity>(iterable);
		return generator;
	}

    private static Generator<Entity> createCSVSourceGenerator(
			ComplexTypeDescriptor complexType, BeneratorContext context, String sourceName) {
		String encoding = complexType.getEncoding();
		if (encoding == null)
		    encoding = context.getDefaultEncoding();
		Converter<String, String> scriptConverter = DescriptorUtil.createStringScriptConverter(context);
		char separator = DescriptorUtil.getSeparator(complexType, context);
	    DataSourceProvider<Entity> fileProvider = new CSVEntitySourceProvider(complexType, scriptConverter, 
	    		separator, encoding);
	    return createEntitySourceGenerator(complexType, context, sourceName, fileProvider);
	}
    
    private static Generator<Entity> createXLSSourceGenerator(
			ComplexTypeDescriptor complexType, BeneratorContext context, String sourceName) {
    	XLSEntitySourceProvider fileProvider = new XLSEntitySourceProvider(
	    		complexType, new ScriptConverterForStrings(context));
		return createEntitySourceGenerator(complexType, context, sourceName, fileProvider);
	}

	private static Generator<Entity> createSimpleTypeEntityGenerator(ComplexTypeDescriptor complexType,
            Uniqueness ownerUniqueness, BeneratorContext context) {
	    TypeDescriptor contentType = complexType.getComponent(ComplexTypeDescriptor.__SIMPLE_CONTENT).getTypeDescriptor();
	    Generator<?> generator = MetaGeneratorFactory.createTypeGenerator(
	    		contentType, complexType.getName(), false, ownerUniqueness, context);
	    return new SimpleTypeEntityGenerator(generator, complexType);
    }
	
	@SuppressWarnings("unchecked")
	public static List<GeneratorComponent<Entity>> createMutatingGeneratorComponents(ComplexTypeDescriptor descriptor,
            Uniqueness ownerUniqueness, BeneratorContext context) {
	    List<GeneratorComponent<Entity>> generatorComponents = new ArrayList<GeneratorComponent<Entity>>();
        for (InstanceDescriptor part : descriptor.getDeclaredParts())
            if (!(part instanceof ComponentDescriptor) || ((ComponentDescriptor) part).getMode() != Mode.ignored && !ComplexTypeDescriptor.__SIMPLE_CONTENT.equals(part.getName())) {
            	try {
                	GeneratorComponent<Entity> generatorComponent = (GeneratorComponent<Entity>) GeneratorComponentFactory.createGeneratorComponent(part, ownerUniqueness, context);
    	            generatorComponents.add(generatorComponent);
            	} catch (Exception e) {
            		throw new ConfigurationError("Error creating component builder for " + part, e);
            	}
            }
	    return generatorComponents;
    }
	
	private static Generator<Entity> createEntitySourceGenerator(ComplexTypeDescriptor complexType,
            BeneratorContext context, String sourceName, DataSourceProvider<Entity> factory) {
	    Generator<Entity> generator = SourceFactory.createRawSourceGenerator(complexType.getNesting(), complexType.getDataset(), sourceName, factory, Entity.class, context);
		generator = WrapperFactory.applyConverter(generator, new ComponentTypeConverter(complexType));
		return generator;
    }

}

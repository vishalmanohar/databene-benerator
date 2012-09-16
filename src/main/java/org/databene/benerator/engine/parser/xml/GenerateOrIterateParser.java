/*
 * (c) Copyright 2009-2012 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine.parser.xml;

import static org.databene.benerator.engine.DescriptorConstants.*;
import static org.databene.benerator.parser.xml.XmlDescriptorParser.parseStringAttribute;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.databene.benerator.Consumer;
import org.databene.benerator.Generator;
import org.databene.benerator.composite.GeneratorComponent;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.CurrentProductGeneration;
import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.expression.CachedExpression;
import org.databene.benerator.engine.expression.xml.XMLConsumerExpression;
import org.databene.benerator.engine.statement.ConversionStatement;
import org.databene.benerator.engine.statement.GenerateAndConsumeTask;
import org.databene.benerator.engine.statement.GenerateOrIterateStatement;
import org.databene.benerator.engine.statement.LazyStatement;
import org.databene.benerator.engine.statement.TimedGeneratorStatement;
import org.databene.benerator.engine.statement.ValidationStatement;
import org.databene.benerator.factory.DescriptorUtil;
import org.databene.benerator.factory.GeneratorComponentFactory;
import org.databene.benerator.factory.MetaGeneratorFactory;
import org.databene.benerator.parser.ModelParser;
import org.databene.commons.CollectionUtil;
import org.databene.commons.Context;
import org.databene.commons.Converter;
import org.databene.commons.ErrorHandler;
import org.databene.commons.StringUtil;
import org.databene.commons.Validator;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.data.ArrayTypeDescriptor;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DescriptorProvider;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.data.Uniqueness;
import org.databene.model.data.VariableHolder;
import org.databene.script.DatabeneScriptParser;
import org.databene.script.Expression;
import org.databene.script.expression.ConstantExpression;
import org.databene.script.expression.DynamicExpression;
import org.databene.script.PrimitiveType;
import org.databene.task.PageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * Parses a &lt;generate&gt; or &lt;update&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 01:05:18
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class GenerateOrIterateParser extends AbstractBeneratorDescriptorParser {
	
	private static final Logger logger = LoggerFactory.getLogger(GenerateOrIterateParser.class);

	private static final Set<String> OPTIONAL_ATTRIBUTES = CollectionUtil.toSet(
			ATT_COUNT, ATT_MIN_COUNT, ATT_MAX_COUNT, ATT_COUNT_DISTRIBUTION, 
			ATT_PAGESIZE, /*ATT_THREADS,*/ ATT_STATS, ATT_ON_ERROR,
			ATT_TEMPLATE, ATT_CONSUMER, 
			ATT_NAME, ATT_TYPE, ATT_CONTAINER, ATT_GENERATOR, ATT_VALIDATOR, 
			ATT_CONVERTER, ATT_NULL_QUOTA, ATT_UNIQUE, ATT_DISTRIBUTION, ATT_CYCLIC,
			ATT_SOURCE, ATT_OFFSET, ATT_SEPARATOR, ATT_ENCODING, ATT_SELECTOR, ATT_SUB_SELECTOR, 
			ATT_DATASET, ATT_NESTING, ATT_LOCALE, ATT_FILTER
		);
	
	//private static final Set<String> PART_ELEMENTS = CollectionUtil.toSet(
	//		EL_VARIABLE, EL_VALUE, EL_ID, EL_COMPOSITE_ID, EL_ATTRIBUTE, EL_REFERENCE, EL_CONSUMER);
	
	private static final Set<String> CONSUMER_EXPECTING_ELEMENTS = CollectionUtil.toSet(EL_GENERATE, EL_ITERATE);

	// DescriptorParser interface --------------------------------------------------------------------------------------
	
	public GenerateOrIterateParser() {
		super("", null, OPTIONAL_ATTRIBUTES);
	}

	@Override
	public boolean supports(Element element, Statement[] parentPath) {
		String name = element.getNodeName();
	    return EL_GENERATE.equals(name) || EL_ITERATE.equals(name);
    }
	
	@Override
	public Statement doParse(final Element element, final Statement[] parentPath, 
			final BeneratorParseContext pContext) {
		final boolean looped = AbstractBeneratorDescriptorParser.containsLoop(parentPath);
		final boolean nested = AbstractBeneratorDescriptorParser.containsGeneratorStatement(parentPath);
		Expression<Statement> expression = new DynamicExpression<Statement>() {
			public Statement evaluate(Context context) {
				return parseGenerate(
						element, parentPath, pContext, (BeneratorContext) context, !looped, nested);
            }
			@Override
			public String toString() {
				return XMLUtil.formatShort(element);
			}
		};
		Statement statement = new LazyStatement(expression);
		statement = new TimedGeneratorStatement(getNameOrType(element), statement, createProfilerPath(parentPath, statement), !looped);
		return statement;
	}
	
	// private helpers -------------------------------------------------------------------------------------------------

	private List<String> createProfilerPath(Statement[] parentPath, Statement currentElement) {
		List<String> path = new ArrayList<String>(parentPath != null ? parentPath.length + 1 : 1);
		if (parentPath != null)
			for (int i = 0; i < parentPath.length; i++)
				path.add(parentPath[i].toString());
		path.add(currentElement.toString());
		return path;
	}

	private String getNameOrType(Element element) {
		String result = element.getAttribute(ATT_NAME);
		if (StringUtil.isEmpty(result))
			result = element.getAttribute(ATT_TYPE);
		if (StringUtil.isEmpty(result))
			result = "anonymous";
		return result;
	}
	
    @SuppressWarnings("unchecked")
    public GenerateOrIterateStatement parseGenerate(Element element, Statement[] parentPath, 
    		BeneratorParseContext parsingContext, BeneratorContext context, boolean infoLog, boolean nested) {
    	context.setCurrentProductName(getNameOrType(element));
    	// parse descriptor
	    InstanceDescriptor descriptor = mapDescriptorElement(element, context);
	    
		// parse statement
		Generator<Long> countGenerator = DescriptorUtil.createDynamicCountGenerator(descriptor, 1L, 1L, false, context);
		Expression<Long> pageSize = parsePageSize(element);
		//Expression<Integer> threads = DescriptorParserUtil.parseIntAttribute(ATT_THREADS, element, 1);
		Expression<PageListener> pager = (Expression<PageListener>) DatabeneScriptParser.parseBeanSpec(
				element.getAttribute(ATT_PAGER));
		Expression<ErrorHandler> errorHandler = parseOnErrorAttribute(element, element.getAttribute(ATT_NAME));
		Expression<Long> minCount = DescriptorUtil.getMinCount(descriptor, 0L);
		GenerateOrIterateStatement statement = new GenerateOrIterateStatement(
				countGenerator, minCount, pageSize, pager, /*threads*/ new ConstantExpression<Integer>(1), 
				errorHandler, infoLog, nested, context);
		
		// parse task and sub statements
		GenerateAndConsumeTask task = parseTask(element, parentPath, statement, parsingContext, descriptor, infoLog);
		statement.setTask(task);
    	context.setCurrentProductName(null);
		return statement;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    private GenerateAndConsumeTask parseTask(Element element, Statement[] parentPath, GenerateOrIterateStatement statement, 
    		BeneratorParseContext parseContext, InstanceDescriptor descriptor, boolean infoLog) {
		descriptor.setNullable(false);
		if (infoLog)
			logger.debug("{}", descriptor);
		
		String taskName = descriptor.getName();
		if (taskName == null)
			taskName = descriptor.getLocalType().getSource();
		BeneratorContext context = statement.getContext();
		BeneratorContext childContext = statement.getChildContext();
		String productName = getNameOrType(element);
		
		// calculate statements
		List<Statement> statements = new ArrayList<Statement>();
		
		// create base generator
    	Generator<?> base = MetaGeneratorFactory.createRootGenerator(descriptor, Uniqueness.NONE, context);
		statements.add(new CurrentProductGeneration(productName, base));

		// handle sub elements
		ModelParser parser = new ModelParser(childContext);
		TypeDescriptor type = descriptor.getTypeDescriptor();
		int arrayIndex = 0;
		Element[] childElements = XMLUtil.getChildElements(element);
		Set<String> handledMembers = new HashSet<String>();
		for (int i = 0; i < childElements.length; i++) {
			Element child = childElements[i];
			String childName = XMLUtil.localName(child);
			// handle configured member/variable elements
			InstanceDescriptor componentDescriptor = null;
			if (EL_VARIABLE.equals(childName)) {
				componentDescriptor = parser.parseVariable(child, (VariableHolder) type);
			} else if (COMPONENT_TYPES.contains(childName)) {
				componentDescriptor = parser.parseComponent(child, (ComplexTypeDescriptor) type);
				handledMembers.add(componentDescriptor.getName().toLowerCase());
			} else if (EL_VALUE.equals(childName)) {
				componentDescriptor = parser.parseSimpleTypeArrayElement(child, (ArrayTypeDescriptor) type, arrayIndex++);
			}
			// handle non-member/variable child elements
			if (componentDescriptor != null) {
				GeneratorComponent<?> componentGenerator = GeneratorComponentFactory.createGeneratorComponent(
						componentDescriptor, Uniqueness.NONE, childContext);
				statements.add(componentGenerator);
			} else if (!EL_CONSUMER.equals(childName)) {
				Statement[] subPath = parseContext.createSubPath(parentPath, statement);
				Statement subStatement = parseContext.parseChildElement(child, subPath);
				statements.add(subStatement);
			}
		}
		// if element is a <generate> then add missing members defined in parent descriptors
		if (EL_GENERATE.equals(element.getNodeName())) {
			if (!StringUtil.isEmpty(element.getAttribute(ATT_SOURCE)))
				syntaxError("'source' not allowed in <generate>", element);
			TypeDescriptor pType = type.getParent();
			if (pType instanceof ComplexTypeDescriptor) {
				// calculate insertion index
				int insertionIndex = statements.size() - 1;
				for (; insertionIndex >= 0; insertionIndex--) {
					Statement tmp = statements.get(insertionIndex);
					if (tmp instanceof GeneratorComponent || tmp instanceof CurrentProductGeneration)
						break;
				}
				insertionIndex++;
				// insert generators from parent
				ComplexTypeDescriptor parentType = (ComplexTypeDescriptor) pType;
				for (ComponentDescriptor component : parentType.getComponents()) {
					String componentName = component.getName();
					if (handledMembers.contains(componentName.toLowerCase()))
						continue;
					GeneratorComponent<?> componentGenerator = GeneratorComponentFactory.createGeneratorComponent(
							component, Uniqueness.NONE, childContext);
					statements.add(insertionIndex++, componentGenerator);
				}
			}
		} else { // make sure the <iterate> does not miss a 'source'
			if (StringUtil.isEmpty(element.getAttribute(ATT_SOURCE)))
				syntaxError("'source' mising in <iterate>", element);
		}
		
		// create task
		GenerateAndConsumeTask task = new GenerateAndConsumeTask(taskName, productName);
		task.setStatements(statements);

		// parse converter
		Converter converter = DescriptorUtil.getConverter(element.getAttribute(ATT_CONVERTER), context);
		if (converter != null)
			task.addStatement(new ConversionStatement(converter));

		// parse validator
		Validator validator = DescriptorUtil.getValidator(element.getAttribute(ATT_VALIDATOR), context);
		if (validator != null)
			task.addStatement(new ValidationStatement(validator));

		// parse consumers
		boolean consumerExpected = CONSUMER_EXPECTING_ELEMENTS.contains(element.getNodeName());
		Expression consumer = parseConsumers(element, consumerExpected, task.getResourceManager());
		task.setConsumer(consumer);
		
		return task;
    }

	private Expression<Consumer> parseConsumers(Element entityElement, boolean consumersExpected, ResourceManager resourceManager) {
		return new CachedExpression<Consumer>(new XMLConsumerExpression(entityElement, consumersExpected, resourceManager));
	}

	private InstanceDescriptor mapDescriptorElement(Element element, BeneratorContext context) { 
		// TODO v0.7.1 Make Descriptors an abstraction of the XML file content and convert XML -> Descriptors -> Statements
		
		// evaluate type
		String type = parseStringAttribute(element, ATT_TYPE, context, false);
		TypeDescriptor localType;
		DescriptorProvider localDescriptorProvider = context.getLocalDescriptorProvider();
		if (PrimitiveType.ARRAY.getName().equals(type) 
				|| XMLUtil.getChildElements(element, false, EL_VALUE).length > 0) {
			localType = new ArrayTypeDescriptor(element.getAttribute(ATT_NAME), localDescriptorProvider);
		} else {
			TypeDescriptor parentType = context.getDataModel().getTypeDescriptor(type);
			if (parentType != null) {
				type = parentType.getName(); // take over capitalization of the parent
				localType = new ComplexTypeDescriptor(parentType.getName(), localDescriptorProvider, (ComplexTypeDescriptor) parentType);
			} else
				localType = new ComplexTypeDescriptor(type, localDescriptorProvider, "entity");
		}
		
		// assemble instance descriptor
		InstanceDescriptor instance = new InstanceDescriptor(type, localDescriptorProvider, type);
		instance.setLocalType(localType);
		
		// map element attributes
		for (Map.Entry<String, String> attribute : XMLUtil.getAttributes(element).entrySet()) {
			String attributeName = attribute.getKey();
			if (!CREATE_ENTITIES_EXT_SETUP.contains(attributeName)) {
				Object attributeValue = attribute.getValue();
				if (instance.supportsDetail(attributeName))
					instance.setDetailValue(attributeName, attributeValue);
				else
					localType.setDetailValue(attributeName, attributeValue);
			}
		}
		
		DescriptorUtil.parseComponentConfig(element, instance.getLocalType(), context);
		return instance;
	}

}

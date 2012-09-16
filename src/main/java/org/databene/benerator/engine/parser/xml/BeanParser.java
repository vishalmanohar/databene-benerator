/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.BeneratorRootStatement;
import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.statement.BeanStatement;
import org.databene.benerator.engine.statement.IfStatement;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.ConversionException;
import org.databene.commons.ParseException;
import org.databene.commons.StringUtil;
import org.databene.commons.xml.XMLUtil;
import org.databene.script.Assignment;
import org.databene.script.BeanSpec;
import org.databene.script.DatabeneScriptParser;
import org.databene.script.Expression;
import org.databene.script.expression.ExpressionUtil;
import org.databene.script.expression.BeanConstruction;
import org.databene.script.expression.DefaultConstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * Parses a &lt;bean&gt; element.<br/><br/>
 * Created: 25.10.2009 01:09:59
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class BeanParser extends AbstractBeneratorDescriptorParser {
	
	private static final Logger logger =  LoggerFactory.getLogger(BeanParser.class);
	
	public BeanParser() {
	    super(EL_BEAN, CollectionUtil.toSet(ATT_ID), CollectionUtil.toSet(ATT_CLASS, ATT_SPEC), 
	    		BeneratorRootStatement.class, IfStatement.class); 
	    // only allowed in non-loop statements in order to avoid leaks
    }

	@Override
	public BeanStatement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
		try {
			String id = element.getAttribute(ATT_ID);
			ResourceManager resourceManager = context.getResourceManager();
			Expression<?> bean = parseBeanExpression(element);
			return new BeanStatement(id, bean, resourceManager);
		} catch (ConversionException e) {
			throw new ConfigurationError(e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    public static Expression<?> parseBeanExpression(Element element) {
		String id = element.getAttribute(ATT_ID);
        Expression<?> instantiation = null;
        String beanSpec = element.getAttribute(ATT_SPEC);
        String beanClass = element.getAttribute(ATT_CLASS);
        if (!StringUtil.isEmpty(beanSpec)) {
        	try {
		        instantiation = DatabeneScriptParser.parseBeanSpec(beanSpec);
        	} catch (ParseException e) {
        		throw new ConfigurationError("Error parsing bean spec: " + beanSpec, e);
        	}
        } else if (!StringUtil.isEmpty(beanClass)) {
	        logger.debug("Instantiating bean of class " + beanClass + " (id=" + id + ")");
	        instantiation = new DefaultConstruction(beanClass);
        } else
        	syntaxError("bean definition is missing 'class' or 'spec' attribute", element);
        Element[] propertyElements = XMLUtil.getChildElements(element, false, EL_PROPERTY);
		Assignment[] propertyInitializers = mapPropertyDefinitions(propertyElements);
        return new BeanConstruction(instantiation, propertyInitializers);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static BeanSpec resolveBeanExpression(Element element, BeneratorContext context) {
		String id = element.getAttribute(ATT_ID);
        Expression<?> instantiation;
        String beanSpecString = element.getAttribute(ATT_SPEC);
        String beanClass = element.getAttribute(ATT_CLASS);
        boolean ref = false;
        if (!StringUtil.isEmpty(beanSpecString)) {
        	try {
		        BeanSpec spec = DatabeneScriptParser.resolveBeanSpec(beanSpecString, context);
				instantiation = ExpressionUtil.constant(spec.getBean());
				ref = spec.isReference();
        	} catch (ParseException e) {
        		throw new ConfigurationError("Error parsing bean spec: " + beanSpecString, e);
        	}
        } else if (!StringUtil.isEmpty(beanClass)) {
	        logger.debug("Instantiating bean of class " + beanClass + " (id=" + id + ")");
	        instantiation = new DefaultConstruction<Object>(beanClass);
        } else
        	throw new ConfigurationError("Syntax error in definition of bean " + id);
        Element[] propertyElements = XMLUtil.getChildElements(element);
        for (Element propertyElement : propertyElements)
        	if (!EL_PROPERTY.equals(propertyElement.getNodeName()))
        		syntaxError("not a supported bean child element: <" + propertyElement.getNodeName() + ">", 
        				propertyElement);
		Assignment[] propertyInitializers = mapPropertyDefinitions(propertyElements);
		Object result = new BeanConstruction(instantiation, propertyInitializers).evaluate(context);
        return new BeanSpec(result, ref);
	}

	public static Assignment[] mapPropertyDefinitions(Element[] propertyElements) {
		Assignment[] assignments = new Assignment[propertyElements.length];
        for (int i = 0; i < propertyElements.length; i++)
        	assignments[i] = parseProperty(propertyElements[i]);
        return assignments;
    }

	private static Assignment parseProperty(Element propertyElement) {
	    String propertyName = propertyElement.getAttribute("name");
	    Expression<?> value = SettingParser.parseValue(propertyElement);
	    return new Assignment(propertyName, value);
    }

}

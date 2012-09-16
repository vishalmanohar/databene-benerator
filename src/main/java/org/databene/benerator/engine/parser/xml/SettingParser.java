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

import org.databene.benerator.Generator;
import org.databene.benerator.engine.DescriptorConstants;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.expression.ScriptableExpression;
import org.databene.benerator.engine.expression.context.ContextReference;
import org.databene.benerator.engine.statement.IfStatement;
import org.databene.benerator.engine.statement.SetSettingStatement;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.ParseException;
import org.databene.script.Expression;
import org.databene.script.expression.CompositeExpression;
import org.databene.script.expression.DynamicExpression;
import org.databene.script.expression.ExpressionUtil;
import org.databene.script.expression.IsNullExpression;
import org.databene.commons.xml.XMLUtil;
import org.databene.script.DatabeneScriptParser;
import org.w3c.dom.Element;

/**
 * Parses a &lt;Property&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 00:58:53
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class SettingParser extends AbstractBeneratorDescriptorParser {

	public SettingParser() {
	    super(DescriptorConstants.EL_SETTING, CollectionUtil.toSet(ATT_NAME), 
	    		CollectionUtil.toSet(ATT_DEFAULT, ATT_VALUE, ATT_REF, ATT_SOURCE));
    }

    @Override
	public Statement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
		String propertyName = element.getAttribute(ATT_NAME);
		if (element.hasAttribute(ATT_DEFAULT))
			return parseDefault(propertyName, element.getAttribute(ATT_DEFAULT));
		else {
			Expression<?> valueEx = parseValue(element);
			return new SetSettingStatement(propertyName, valueEx);
		}
	}

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Expression<?> parseValue(Element element) {
		if (element.hasAttribute(ATT_VALUE))
			return DescriptorParserUtil.parseScriptableStringAttribute(ATT_VALUE, element);
		else if (element.hasAttribute(ATT_REF))
			return new ContextReference(element.getAttribute(ATT_REF));
		else if (element.hasAttribute(ATT_SOURCE))
			return parseSource(element.getAttribute(ATT_SOURCE));
		else { // map child elements to a collection or array
	        Element[] childElements = XMLUtil.getChildElements(element);
	        Expression[] subExpressions = new Expression[childElements.length];
	        for (int j = 0; j < childElements.length; j++)
	        	subExpressions[j] = BeanParser.parseBeanExpression(childElements[j]);
	        switch (subExpressions.length) {
		        case 0: throw new ConfigurationError("No valid property spec: " + XMLUtil.formatShort(element));
		        case 1: return subExpressions[0];
		        default: return new CompositeExpression<Object, Object>(subExpressions) {
		    		public Object[] evaluate(Context context) {
		                return ExpressionUtil.evaluateAll(terms, context);
		            }
		        };
	        }
	    }
	}

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Expression<?> parseSource(String source) {
		try {
			return new SourceExpression(DatabeneScriptParser.parseBeanSpec(source));
        } catch (ParseException e) {
            throw new ConfigurationError("Error parsing property source expression: " + source, e);
        }
    }

    private static Statement parseDefault(String propertyName, String defaultValue) {
		try {
			ScriptableExpression valueExpression = new ScriptableExpression(defaultValue, null);
			SetSettingStatement setterStatement = new SetSettingStatement(propertyName, valueExpression);
			Expression<Boolean> condition = new IsNullExpression(new ContextReference(propertyName));
			return new IfStatement(condition, setterStatement);
        } catch (ParseException e) {
            throw new ConfigurationError("Error parsing property default value expression: " + defaultValue, e);
        }
    }

	/**
     * Evaluates a 'source' expression to a Generator.<br/><br/>
     * Created: 26.10.2009 08:38:44
     * @since 0.6.0
     * @author Volker Bergmann
     */
    public static class SourceExpression<E> extends DynamicExpression<E> {
    	
    	Expression<Generator<E>> source;
    	
		public SourceExpression(Expression<Generator<E>> source) {
	        this.source = source;
        }

        public E evaluate(Context context) {
			Generator<E> generator = source.evaluate(context);
			ProductWrapper<E> wrapper = generator.generate(new ProductWrapper<E>());
			if (wrapper == null)
				throw new ConfigurationError("Generator not available: " + generator);
			return wrapper.unwrap();
		}

    }

}

/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.engine.expression.ScriptExpression;
import org.databene.benerator.engine.expression.ScriptableExpression;
import org.databene.benerator.engine.expression.TypedScriptExpression;
import org.databene.script.Expression;
import org.databene.commons.StringUtil;
import org.databene.script.expression.ConstantExpression;
import org.databene.script.expression.ConvertingExpression;
import org.databene.script.expression.StringExpression;
import org.databene.script.expression.TypeConvertingExpression;
import org.databene.script.expression.UnescapeExpression;
import org.databene.commons.xml.XMLUtil;
import org.databene.text.SplitStringConverter;
import org.w3c.dom.Element;

/**
 * Provides utility methods for XML descriptor parsing.<br/><br/>
 * Created: 19.02.2010 09:32:33
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DescriptorParserUtil {

	// direct data retrieval -------------------------------------------------------------------------------------------
	
	public static String getAttribute(String name, Element element) {
	    return StringUtil.emptyToNull(element.getAttribute(name));
    }

	public static String getElementText(Element element) {
	    return XMLUtil.getText(element);
    }

	// creating expressions for data retrieval -------------------------------------------------------------------------
	
	public static Expression<String> parseScriptableElementText(Element element, boolean unescape) {
		Expression<String> result = new StringExpression(new ScriptableExpression(XMLUtil.getText(element), null));
		if (unescape)
			result = new UnescapeExpression(result);
		return result;
    }

	public static Expression<String> parseScriptableStringAttribute(String name, Element element) {
	    return parseScriptableStringAttribute(name, element, true);
    }

	public static Expression<String> parseScriptableStringAttribute(String name, Element element, boolean unescape) {
	    String attribute = getAttribute(name, element);
	    if (attribute == null)
	    	return null;
	    Expression<String> result = new StringExpression(new ScriptableExpression(attribute, null));
		if (unescape)
			result = new UnescapeExpression(result);
	    return result;
    }

	public static Expression<String[]> parseScriptableStringArrayAttribute(String name, Element element) {
	    String attribute = getAttribute(name, element);
		if (attribute == null)
			return null;
		Expression<String> rawEx = new TypeConvertingExpression<String>(
				new ScriptableExpression(attribute, null), String.class);
		return new ConvertingExpression<String, String[]>(rawEx, new SplitStringConverter(','));
    }

	public static Expression<Integer> parseIntAttribute(String name, Element element) {
	    return new TypedScriptExpression<Integer>(getAttribute(name, element), Integer.class);
    }

	public static Expression<Integer> parseIntAttribute(String name, Element element, int defaultValue) {
	    return parseIntAttribute(name, element, new ConstantExpression<Integer>(defaultValue));
    }

	public static Expression<Integer> parseIntAttribute(String name, Element element, Expression<Integer> defaultValue) {
	    String attribute = getAttribute(name, element);
	    if (StringUtil.isEmpty(attribute))
	    	return defaultValue;
	    else
	    	return new TypedScriptExpression<Integer>(attribute, Integer.class);
    }

	public static Expression<Long> parseLongAttribute(String name, Element element, long defaultValue) {
	    return parseLongAttribute(name, element, new ConstantExpression<Long>(defaultValue));
    }

	public static Expression<Long> parseLongAttribute(String name, Element element, Expression<Long> defaultValue) {
	    String attribute = getAttribute(name, element);
	    if (StringUtil.isEmpty(attribute))
	    	return defaultValue;
	    else
	    	return new TypedScriptExpression<Long>(attribute, Long.class);
    }

	public static Expression<Boolean> parseBooleanExpressionAttribute(String name, Element element) {
	    return parseBooleanExpressionAttribute(name, element, null);
    }

	public static Expression<Boolean> parseBooleanExpressionAttribute(String name, Element element, Boolean defaultValue) {
	    String attribute = getAttribute(name, element);
	    if (StringUtil.isEmpty(attribute))
	    	return new ConstantExpression<Boolean>(defaultValue);
	    else
	    	return new TypedScriptExpression<Boolean>(attribute, Boolean.class);
    }

	public static ConstantExpression<String> parseAttribute(String name, Element element) {
		String attribute = getAttribute(name, element);
		return (attribute != null ? new ConstantExpression<String>(attribute) : null);
    }

	public static Expression<?> parseScriptAttribute(String name, Element element) {
		String rawAttribute = getAttribute(name, element);
		if (StringUtil.isEmpty(rawAttribute))
			return null;
		else
			return new ScriptExpression<Object>(rawAttribute);
	}
	
}

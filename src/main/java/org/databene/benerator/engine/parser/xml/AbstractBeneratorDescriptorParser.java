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

import static org.databene.benerator.engine.DescriptorConstants.ATT_ON_ERROR;
import static org.databene.benerator.engine.DescriptorConstants.ATT_PAGESIZE;
import static org.databene.benerator.engine.parser.xml.DescriptorParserUtil.parseScriptableStringAttribute;

import java.util.Set;

import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.expression.ErrorHandlerExpression;
import org.databene.benerator.engine.expression.context.DefaultPageSizeExpression;
import org.databene.benerator.engine.statement.GenerateOrIterateStatement;
import org.databene.benerator.engine.statement.RunTaskStatement;
import org.databene.benerator.engine.statement.WhileStatement;
import org.databene.commons.ErrorHandler;
import org.databene.script.Expression;
import org.databene.webdecs.xml.AbstractXMLElementParser;
import org.databene.webdecs.xml.ParseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * Abstract parent class for Descriptor parsers.<br/><br/>
 * Created: 25.10.2009 00:43:18
 * @since 0.6.0
 * @author Volker Bergmann
 */
public abstract class AbstractBeneratorDescriptorParser extends AbstractXMLElementParser<Statement> {
	
	protected Logger logger = LoggerFactory.getLogger(AbstractBeneratorDescriptorParser.class);

	public AbstractBeneratorDescriptorParser(String elementName, 
			Set<String> requiredAttributes, Set<String> optionalAttributes, Class<?>... supportedParentTypes) {
		super(elementName, requiredAttributes, optionalAttributes, supportedParentTypes);
    }

	@Override
	public final Statement doParse(Element element, Statement[] parentPath, ParseContext<Statement> context) {
		return doParse(element, parentPath, (BeneratorParseContext) context);
	}

	public abstract Statement doParse(Element element, Statement[] parentPath, BeneratorParseContext context);
	
	public static boolean containsLoop(Statement[] parentPath) {
		if (parentPath == null)
			return false;
		for (Statement statement : parentPath)
			if (isLoop(statement))
				return true;
		return false;
	}

	public static boolean isLoop(Statement statement) {
	    return (statement instanceof RunTaskStatement) 
	    	|| (statement instanceof GenerateOrIterateStatement)
	    	|| (statement instanceof WhileStatement);
    }
	
	public static boolean containsGeneratorStatement(Statement[] parentPath) {
		if (parentPath == null)
			return false;
		for (Statement statement : parentPath)
			if (statement instanceof GenerateOrIterateStatement)
				return true;
		return false;
    }

	protected Expression<ErrorHandler> parseOnErrorAttribute(Element element, String id) {
	    return new ErrorHandlerExpression(id, parseScriptableStringAttribute(ATT_ON_ERROR, element));
    }

	protected Expression<Long> parsePageSize(Element element) {
		return DescriptorParserUtil.parseLongAttribute(ATT_PAGESIZE, element, new DefaultPageSizeExpression());
	}

}

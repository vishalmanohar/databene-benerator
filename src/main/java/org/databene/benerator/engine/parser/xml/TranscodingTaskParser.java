/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
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

import static org.databene.benerator.engine.parser.xml.DescriptorParserUtil.*;

import static org.databene.benerator.engine.DescriptorConstants.*;

import org.databene.benerator.engine.BeneratorRootStatement;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.statement.IfStatement;
import org.databene.benerator.engine.statement.TranscodingTaskStatement;
import org.databene.benerator.engine.statement.WhileStatement;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ErrorHandler;
import org.databene.script.Expression;
import org.databene.platform.db.DBSystem;
import org.w3c.dom.Element;

/**
 * Parses Benerator's &lt;transcode&gt; XML descriptor element.<br/><br/>
 * Created: 10.09.2010 18:14:53
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class TranscodingTaskParser extends AbstractTranscodeParser {

	public TranscodingTaskParser() {
	    super(EL_TRANSCODING_TASK, 
	    		CollectionUtil.toSet(ATT_TARGET), 
	    		CollectionUtil.toSet(ATT_IDENTITY, ATT_DEFAULT_SOURCE, ATT_PAGESIZE, ATT_ON_ERROR), 
	    		BeneratorRootStatement.class, IfStatement.class, WhileStatement.class);
    }

    @Override
    public Statement doParse(Element element, Statement[] parentPath, BeneratorParseContext parsingContext) {
    	Expression<ErrorHandler> errorHandlerExpression = parseOnErrorAttribute(element, "transcodingTask");
		TranscodingTaskStatement statement = new TranscodingTaskStatement(
				parseDefaultSource(element), 
				parseTarget(element), 
				parseIdentity(element), 
				parsePageSize(element), 
	    		errorHandlerExpression);
		Statement[] subPath = parsingContext.createSubPath(parentPath, statement);
		statement.setSubStatements(parsingContext.parseChildElementsOf(element, subPath));
		return statement;
    }

	private Expression<String> parseIdentity(Element element) {
		return parseScriptableStringAttribute("identity", element);
	}

	@SuppressWarnings("unchecked")
    protected Expression<DBSystem> parseDefaultSource(Element element) {
	    Expression<DBSystem> sourceEx = (Expression<DBSystem>) parseScriptAttribute("defaultSource", element);
	    return sourceEx;
    }

}

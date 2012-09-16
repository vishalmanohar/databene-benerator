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

import static org.databene.benerator.engine.DescriptorConstants.ATT_TEST;
import static org.databene.benerator.engine.DescriptorConstants.EL_IF;
import static org.databene.benerator.engine.DescriptorConstants.EL_SETUP;
import static org.databene.benerator.engine.DescriptorConstants.EL_WHILE;
import static org.databene.benerator.engine.parser.xml.DescriptorParserUtil.*;

import java.util.List;
import java.util.Set;

import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.statement.WhileStatement;
import org.databene.commons.CollectionUtil;
import org.databene.script.Expression;
import org.w3c.dom.Element;

/**
 * Parses a 'while' element.<br/><br/>
 * Created: 19.02.2010 09:18:47
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class WhileParser extends AbstractBeneratorDescriptorParser {

	private static final Set<String> LEGAL_PARENTS = CollectionUtil.toSet(
			EL_SETUP, EL_IF, EL_WHILE);

	public WhileParser() {
		super(EL_WHILE, CollectionUtil.toSet(ATT_TEST), null);
	}

    public boolean supports(String elementName, String parentName) {
	    return (EL_WHILE.equals(elementName) && LEGAL_PARENTS.contains(parentName));
    }

	@Override
	public Statement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
		Expression<Boolean> condition = parseBooleanExpressionAttribute(ATT_TEST, element);
		WhileStatement whileStatement = new WhileStatement(condition);
		List<Statement> subStatements = context.parseChildElementsOf(element, context.createSubPath(parentPath, whileStatement));
		whileStatement.setSubStatements(subStatements);
	    return whileStatement;
    }

}

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

import java.util.Set;

import org.databene.benerator.engine.BeneratorRootStatement;
import org.databene.benerator.engine.DescriptorConstants;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.expression.ScriptableExpression;
import org.databene.benerator.engine.statement.IfStatement;
import org.databene.benerator.engine.statement.IncludeStatement;
import org.databene.commons.CollectionUtil;
import org.databene.script.Expression;
import org.databene.script.expression.StringExpression;
import org.w3c.dom.Element;
import static org.databene.benerator.engine.DescriptorConstants.*;

/**
 * Parses an <lt;include&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 00:32:02
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class IncludeParser extends AbstractBeneratorDescriptorParser {
	
	static final Set<String> REQUIRED_ATTRIBUTES = CollectionUtil.toSet(ATT_URI);

	public IncludeParser() {
	    super(EL_INCLUDE, REQUIRED_ATTRIBUTES, null, 
	    		BeneratorRootStatement.class, IfStatement.class);
    }

	@Override
	public IncludeStatement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
        String uriAttr = element.getAttribute(DescriptorConstants.ATT_URI);
		Expression<String> uriEx = new StringExpression(new ScriptableExpression(uriAttr, null));
        return new IncludeStatement(uriEx);
    }

}

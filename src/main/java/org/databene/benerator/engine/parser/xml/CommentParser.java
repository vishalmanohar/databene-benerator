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

import org.databene.benerator.engine.DescriptorConstants;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.statement.CommentStatement;
import org.databene.commons.xml.XMLUtil;
import org.w3c.dom.Element;

/**
 * Parses a &lt;comment&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 00:27:00
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class CommentParser extends AbstractBeneratorDescriptorParser {

	public CommentParser() {
	    super(DescriptorConstants.EL_COMMENT, null, null);
    }

	@Override
	public CommentStatement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
	    return new CommentStatement(XMLUtil.getText(element).trim());
    }

}

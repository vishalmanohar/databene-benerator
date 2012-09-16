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

import java.util.Collection;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.BeneratorRootStatement;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.statement.IfStatement;
import org.databene.benerator.parser.ModelParser;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.data.ComponentDescriptor;
import org.w3c.dom.Element;
import static org.databene.benerator.engine.DescriptorConstants.*;

/**
 * Parses a &lt;defaultComponents&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 00:17:04
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DefaultComponentParser extends AbstractBeneratorDescriptorParser {

	static final Collection<String> COMPONENT_TYPES = CollectionUtil.toSet("attribute", "part", "id", "reference");

	public DefaultComponentParser() {
		super(EL_DEFAULT_COMPONENTS, null, null, 
			BeneratorRootStatement.class, IfStatement.class);
	}

	@Override
	public XMLDefaultComponentsStatement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
		return new XMLDefaultComponentsStatement(element);
	}

	class XMLDefaultComponentsStatement implements Statement {
		
		private Element element;

	    public XMLDefaultComponentsStatement(Element element) {
	    	this.element = element;
	    }

		public boolean execute(BeneratorContext context) {
			for (Element child : XMLUtil.getChildElements(element)) {
				String childType = XMLUtil.localName(child);
				if (COMPONENT_TYPES.contains(childType)) {
					ModelParser parser = new ModelParser(context);
					ComponentDescriptor component = parser.parseSimpleTypeComponent(child, null, null);
					context.setDefaultComponentConfig(component);
				} else
					throw new ConfigurationError("Unexpected element: " + childType);
			}
	    	return true;
		}

	}

}

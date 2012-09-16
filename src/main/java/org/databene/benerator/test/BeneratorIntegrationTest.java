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

package org.databene.benerator.test;

import java.io.IOException;

import org.databene.benerator.BeneratorFactory;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.benerator.engine.ResourceManagerSupport;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.parser.xml.BeneratorParseContext;
import org.databene.commons.IOUtil;
import org.databene.commons.xml.XMLUtil;
import org.junit.After;
import org.junit.Before;
import org.w3c.dom.Element;

/**
 * Parent class for Benerator integration tests.<br/><br/>
 * Created: 10.08.2010 07:07:42
 * @since 0.6.4
 * @author Volker Bergmann
 */
public abstract class BeneratorIntegrationTest extends GeneratorTest {
	
	@Before
	public void setUpEnvironment() throws Exception {
		System.setProperty(DefaultBeneratorContext.CELL_SEPARATOR_SYSPROP, ",");
	}

	@After
	public void tearDown() {
		System.setProperty(DefaultBeneratorContext.CELL_SEPARATOR_SYSPROP, ",");
	}

	protected BeneratorContext parseAndExecuteFile(String filename) throws IOException {
		String xml = IOUtil.getContentOfURI(filename);
		return parseAndExecute(xml);
    }

	protected BeneratorContext parseAndExecuteRoot(String xml) {
		context = new DefaultBeneratorContext();
	    Statement statement = parse(xml);
		statement.execute(context);
		return context;
    }

	protected BeneratorContext parseAndExecute(String xml) {
	    Statement statement = parse(xml);
		statement.execute(context);
		return context;
    }

	public Statement parse(String xml) {
		Element element = XMLUtil.parseStringAsElement(xml);
		ResourceManagerSupport resourceManager = new ResourceManagerSupport();
		BeneratorParseContext parsingContext = BeneratorFactory.getInstance().createParseContext(resourceManager);
		return parsingContext.parseElement(element, null);
	}
	
}

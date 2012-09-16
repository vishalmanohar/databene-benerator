/*
 * (c) Copyright 2008-2011 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
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

package org.databene.benerator.xml;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.databene.benerator.file.XMLFileGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.commons.IOUtil;
import org.databene.commons.xml.XMLUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests the XMLFileGenerator.<br/><br/>
 * Created: 06.03.2008 11:16:45
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class XMLFileGeneratorTest extends GeneratorTest {
    
	private static final String SIMPLE_ELEMENT_TEST_XSD = "org/databene/platform/xml/simple-element-test.xsd";
	private static final String BEAN_TEST_XSD = "org/databene/benerator/xml/bean_test.xsd";
    //private static final String VARIABLE_TEST_XSD = "org/databene/benerator/xml/variable_test.xsd";

    private static final Logger logger = LoggerFactory.getLogger(XMLFileGeneratorTest.class);
    
    @Test
    public void testSimpleTypeElement() throws IOException {
        createXMLFile(SIMPLE_ELEMENT_TEST_XSD, "root", "target/simple-element-test.xml");
    }
    
    @Test
    public void testBean() throws IOException {
        Document document = createXMLFile(BEAN_TEST_XSD, "root", "target/bean_test.xml");
        Element root = document.getDocumentElement();
        assertEquals("root", root.getNodeName());
        Element[] children = XMLUtil.getChildElements(root);
        assertEquals(1, children.length);
        assertElementNameAndText(children[0], "result", "OK");
    }
	
    /* TODO v0.8 support variables in XML Schema-based generation
    @Test
    public void testVariables() throws IOException {
        Document document = createXMLFile(VARIABLE_TEST_XSD, "root", "target/variable_test.xml");
        Element root = document.getDocumentElement();
        assertEquals("root", root.getNodeName());
        assertEquals(0, root.getChildNodes().getLength());
        assertEquals("OK", root.getAttribute("string_att"));
        assertEquals("Bob", root.getAttribute("bean_att"));
        assertEquals("Alice", root.getAttribute("entity_att"));
    }
    */
    
    // private helpers -------------------------------------------------------------------------------------------------

    private void assertElementNameAndText(Element child, String name, String text) {
        assertNotNull(child);
        assertEquals(name, child.getNodeName());
        assertEquals(text, XMLUtil.getText(child));
    }

    private Document createXMLFile(String schemaUri, String root, String filename) throws IOException {
    	context.setContextUri(IOUtil.getParentUri(schemaUri));
        XMLFileGenerator generator = new XMLFileGenerator(schemaUri, root, filename);
        generator.init(context);
        File file = GeneratorUtil.generateNonNull(generator);
        assertNotNull(file);
        logger.debug("Generated " + file);
        generator.close();
        // validate the generated file
        Document document = XMLUtil.parse(file.getAbsolutePath());
        return document;
    }

}

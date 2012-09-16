/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.dbunit;

import static org.junit.Assert.*;

import java.io.File;

import org.databene.benerator.test.ModelTest;
import org.databene.commons.FileUtil;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.data.Entity;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests the {@link DbUnitEntityExporter}.<br/><br/>
 * Created: 05.11.2009 07:23:45
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DbUnitEntityExporterTest extends ModelTest {

	private static final String FILENAME = "target/" + DbUnitEntityExporterTest.class.getName() + ".dbunit.xml";
	private Entity ALICE;
	private Entity BOB;

	@Before
	public void setUpExpectedEntities() {
		ALICE = createEntity("Person", "name", "Alice", "age", 23);
		BOB = createEntity("Person", "name", "Bob", "age", 34);
	}
	
	@Test
	public void test() throws Exception {
		DbUnitEntityExporter exporter = new DbUnitEntityExporter(FILENAME);
		exporter.startProductConsumption(ALICE);
		exporter.startProductConsumption(BOB);
		exporter.finishProductConsumption(BOB);
		exporter.finishProductConsumption(ALICE);
		exporter.close();
		Document doc = XMLUtil.parse(FILENAME);
		Element root = doc.getDocumentElement();
		Element[] children = XMLUtil.getChildElements(root);
		assertEquals(2, children.length);
		assertPerson(children[0], "Alice", 23);
		assertPerson(children[1], "Bob",   34);
		FileUtil.deleteIfExists(new File(FILENAME));
	}

	@Test
	public void testClosingTwice() throws Exception {
		DbUnitEntityExporter exporter = new DbUnitEntityExporter(FILENAME);
		exporter.startProductConsumption(ALICE);
		exporter.finishProductConsumption(ALICE);
		exporter.close();
		exporter.close();
	}

	@Test
	public void testUnusedClose() throws Exception {
		DbUnitEntityExporter exporter = new DbUnitEntityExporter(FILENAME);
		exporter.close();
	}

	private void assertPerson(Element element, String name, int age) {
	    assertEquals("Person", element.getNodeName());
		assertEquals(name, element.getAttribute("name"));
		assertEquals(String.valueOf(age), element.getAttribute("age"));
    }
	
}

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

package org.databene.benerator.engine.statement;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.BeneratorRootStatement;
import org.databene.benerator.engine.DescriptorRunner;
import org.databene.benerator.factory.EquivalenceGeneratorFactory;
import org.databene.benerator.test.BeneratorIntegrationTest;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ConfigurationError;
import org.databene.model.data.Entity;
import org.junit.Test;

/**
 * Tests the {@link BeneratorRootStatement}.<br/><br/>
 * Created: 24.10.2009 11:22:25
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class BeneratorRootStatementTest extends BeneratorIntegrationTest {

	@Test
	public void testGeneratorFactoryConfig() {
		Map<String, String> attributes = CollectionUtil.buildMap(
				"generatorFactory", EquivalenceGeneratorFactory.class.getName());
		BeneratorRootStatement root = new BeneratorRootStatement(attributes);
		root.execute(context);
		assertEquals(EquivalenceGeneratorFactory.class, context.getGeneratorFactory().getClass());
	}
	
	@Test
	public void testGetGenerator_simple() throws Exception {
        check("org/databene/benerator/engine/statement/simple.ben.xml");
	}

	@Test
	public void testGetGenerator_include() throws Exception {
        check("org/databene/benerator/engine/statement/including.ben.xml");
	}
	
	@Test
	public void testDefaultImports_default() throws Exception {
		// given the default settings
		String xml = "<setup/>";
		// when executing the RootStatement
		BeneratorContext context = parseAndExecuteRoot(xml);
		// then the default imports should have been applied, 
		// thus org.databene.benerator.consumer.ConsoleExporter can be found
	    context.forName("ConsoleExporter");
	}
	
	@Test
	public void testDefaultImports_true() throws Exception {
		// given that defaults import is requested explicitly
		String xml = "<setup defaultImports='true'/>";
		// when executing the RootStatement
		BeneratorContext context = parseAndExecuteRoot(xml);
		// then org.databene.benerator.consumer.ConsoleExporter can be found
	    context.forName("ConsoleExporter");
	}

	@Test(expected = ConfigurationError.class)
	public void testDefaultImports_false() throws Exception {
		// given that defaults import is disabled
		String xml = "<setup defaultImports='false'/>";
		// when executing the RootStatement
		BeneratorContext context = parseAndExecuteRoot(xml);
		// then the default imports have not been applied, 
		// and org.databene.benerator.consumer.ConsoleExporter cannot be found
	    context.forName("ConsoleExporter");
	}

	// helpers ---------------------------------------------------------------------------------------------------------

	private void check(String uri) throws IOException {
		DescriptorRunner runner = new DescriptorRunner(uri, context);
	    BeneratorRootStatement statement = runner.parseDescriptorFile();
        Generator<?> generator = statement.getGenerator("Person", runner.getContext());
		assertEquals(Object.class, generator.getGeneratedType());
        assertNotNull(generator);
        generator.init(context);
        for (int i = 0; i < 3; i++)
        	checkGeneration(generator);
        assertUnavailable(generator);
        generator.close();
    }

	private void checkGeneration(Generator<?> generator) {
	    Entity entity = (Entity) GeneratorUtil.generateNonNull(generator);
	    assertNotNull("generator unavailable: " + generator, entity);
		assertEquals("Person", entity.type());
		assertEquals("Alice", entity.get("name"));
    }
	
}

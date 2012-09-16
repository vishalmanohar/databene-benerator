/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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
import static org.junit.Assert.assertNotNull;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.DescriptorBasedGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.commons.SystemInfo;
import org.databene.model.data.Entity;
import org.junit.Test;

/**
 * Tests the {@link DescriptorBasedGenerator}.<br/><br/>
 * Created: 23.02.2010 12:17:27
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DescriptorBasedGeneratorTest extends GeneratorTest {

	@Test
	public void testGetGenerator() throws Exception {
		String lf = SystemInfo.getLineSeparator();
		String uri = "string://<setup>" + lf +
				"	<generate name='perGen' type='Person' count='3'>" + lf +
				"		<id name='id' type='int'/>" + lf +
				"		<attribute name='name' constant='Alice'/>" + lf +
				"	</generate>" + lf +
				"</setup>";
		context.setValidate(false);
		Generator<?> generator = new DescriptorBasedGenerator(uri, "perGen", context);
		assertEquals(Object.class, generator.getGeneratedType());
		assertNotNull(generator);
		generator.init(context);
		for (int i = 0; i < 3; i++)
			checkGeneration((Entity) GeneratorUtil.generateNonNull(generator), i + 1);
		assertUnavailable(generator);
		generator.close();
	}

	private void checkGeneration(Entity entity, int id) {
	    assertEquals(createEntity("Person", "id", id, "name", "Alice"), entity);
    }
	
}

/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.databene.benerator.test.BeneratorIntegrationTest;
import org.databene.benerator.test.ConsumerMock;
import org.databene.model.data.Entity;
import org.junit.Test;

/**
 * Tests the &lt;defaultComponents&gt; element.<br/><br/>
 * Created: 23.05.2011 09:37:37
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class DefaultComponentIntegrationTest extends BeneratorIntegrationTest {

	@Test
	public void testStandardIntegration() throws Exception {
		checkFile("org/databene/benerator/engine/defaultComponent-std.ben.xml");
	}
	
	@Test
	public void testDbIntegration() throws Exception {
		checkFile("org/databene/benerator/engine/defaultComponent-db.ben.xml");
	}

	@SuppressWarnings("unchecked")
	public void checkFile(String uri) throws IOException {
		ConsumerMock consumer = new ConsumerMock(true);
		context.set("cons", consumer);
		new DescriptorRunner(uri, context).run();
		List<Entity> products = (List<Entity>) consumer.getProducts();
		long currentMillies = System.currentTimeMillis();
		for (Entity product : products) {
			// check created_by
			String createdBy = (String) product.get("created_by");
			assertEquals("Bob", createdBy);
			// check created_at
			Date creationDate = (Date) product.get("created_at");
			assertNotNull(creationDate);
			long productMillies = creationDate.getTime();
			assertTrue(Math.abs(productMillies - currentMillies) < 3000);
		}
	}
	
}

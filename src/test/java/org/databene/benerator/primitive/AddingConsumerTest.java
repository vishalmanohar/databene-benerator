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

package org.databene.benerator.primitive;

import static org.junit.Assert.*;

import java.io.IOException;

import org.databene.benerator.engine.DescriptorRunner;
import org.databene.benerator.test.GeneratorTest;
import org.databene.model.data.Entity;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link AddingConsumer}.<br/><br/>
 * Created: 04.04.2010 08:03:25
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class AddingConsumerTest extends GeneratorTest {
	
	private Entity ALICE;
	private Entity METHUSALEM;
	
	@Before
	public void setUpPersons() {
		ALICE = createEntity("Person", "age", 23L); // long age
		METHUSALEM = createEntity("Person", "age", 1024.); // double age
	}
	
	@Test
	public void testJavaInvocation() {
		AddingConsumer consumer = new AddingConsumer();
		consumer.setFeature("age");
		consumer.setType("int");
		consumer.startProductConsumption(ALICE);
		consumer.finishProductConsumption(ALICE);
		consumer.startProductConsumption(METHUSALEM);
		consumer.finishProductConsumption(METHUSALEM);
		assertEquals(1047, consumer.getSum());
	}
	
	@Test
	public void testBeneratorInvocation() throws IOException {
		new DescriptorRunner("org/databene/benerator/primitive/AddingConsumerTest.ben.xml", context).run();
	}

}

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

package org.databene.benerator.consumer;

import static org.junit.Assert.*;

import org.databene.benerator.consumer.MappingEntityConsumer;
import org.databene.benerator.factory.ConsumerMock;
import org.databene.benerator.test.ModelTest;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.model.data.Entity;
import org.junit.Test;

/**
 * Tests the {@link MappingEntityConsumer}.<br/><br/>
 * Created: 22.02.2010 20:09:02
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class MappingEntityConsumerTest extends ModelTest {

	@Test
	public void test() {
		ConsumerMock target = new ConsumerMock();
		MappingEntityConsumer consumer = new MappingEntityConsumer();
		consumer.setTarget(target);
		consumer.setMappings("'name'->'givenName', 'none'->'some'");
		
		Entity input = createEntity("Person", "name", "Alice", "age", 23);
		consumer.startConsuming(new ProductWrapper<Entity>().wrap(input));
		consumer.finishConsuming(new ProductWrapper<Entity>().wrap(input));
		assertEquals(createEntity("Person", "givenName", "Alice", "age", 23), target.lastProduct);
	}
	
}

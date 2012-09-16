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

package org.databene.benerator.engine.parser.xml;

import static org.junit.Assert.*;

import java.io.IOException;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.DescriptorRunner;
import org.databene.benerator.factory.ConsumerMock;
import org.databene.benerator.test.GeneratorTest;
import org.databene.commons.SysUtil;
import org.databene.model.data.Entity;
import org.junit.Test;

/**
 * Tests access to Benerator's InitialContext in descriptor file.<br/><br/>
 * Created: 21.10.2009 19:25:37
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class InitialContextTest extends GeneratorTest {

	private static final String DESCRIPTOR_XML = "string://<setup>" +
		                    		"<bean id='ctx' class='org.databene.platform.jndi.InitialContext'>" +
		                    		"  <property name='factory' value='org.databene.platform.jndi.InitialContextFactoryMock' />" +
		                    		"  <property name='url' value='lru' />" +
		                    		"  <property name='user' value='resu' />" +
		                    		"  <property name='password' value='drowssap' />" +
		                    		"</bean>" +
		                    		"<generate name='Person' count='1' consumer=\"ctx.lookup('cons')\">" +
		                    		"  <attribute name='name' constant='Alice'/>" +
		                    		"</generate>" +
		                    		"</setup>";

	@Test
	public void test() {
		SysUtil.runWithSystemProperty("jndi.properties", "org/databene/benerator/engine/jndi.properties", 
			new Runnable() {
				public void run() {
					try {
						ConsumerMock.lastInstance = null;
	                    DescriptorRunner runner = new DescriptorRunner(DESCRIPTOR_XML, context);
	                    
	                    BeneratorContext context = runner.getContext();
	                    context.setValidate(false);
	                    runner.run();
	                    assertNotNull("Consumer was not invoked", ConsumerMock.lastInstance.lastProduct);
	                    assertEquals("Alice", ((Entity) ConsumerMock.lastInstance.lastProduct).get("name"));
                    } catch (IOException e) {
	                    throw new RuntimeException(e);
                    }
                }
		});
	}
	
}

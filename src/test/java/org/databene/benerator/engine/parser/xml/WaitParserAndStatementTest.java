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

package org.databene.benerator.engine.parser.xml;

import static org.junit.Assert.*;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.benerator.engine.statement.WaitStatement;
import org.databene.commons.xml.XMLUtil;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * Tests {@link WaitParser} and {@link WaitStatement}.<br/><br/>
 * Created: 21.02.2010 08:04:48
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class WaitParserAndStatementTest {
	
	@Test
	public void testConstantDuration() throws Exception {
		Element element = XMLUtil.parseStringAsElement("<wait duration='12'/>");
		BeneratorContext context = new DefaultBeneratorContext();
		WaitStatement statement = (WaitStatement) new WaitParser().parse(element, null, null);
		assertEquals(12, statement.generateDuration(context));
		statement.execute(context);
	}

	@Test
	public void testDistributedDuration() throws Exception {
		Element element = XMLUtil.parseStringAsElement(
				"<wait min='11' max='25' granularity='2' distribution='step'/>");
		BeneratorContext context = new DefaultBeneratorContext();
		WaitStatement statement = (WaitStatement) new WaitParser().parse(element, null, null);
		for (int i = 0; i < 5; i++)
			assertEquals(11 + i * 2, statement.generateDuration(context));
		statement.execute(context);
	}

}

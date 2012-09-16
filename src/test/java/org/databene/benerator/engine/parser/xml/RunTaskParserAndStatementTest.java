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

package org.databene.benerator.engine.parser.xml;

import static org.junit.Assert.*;

import org.databene.benerator.engine.statement.RunTaskStatement;
import org.databene.benerator.test.BeneratorIntegrationTest;
import org.databene.task.PageListenerMock;
import org.databene.task.TaskMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Parses an XML &lt;run-task&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 26.10.2009 07:07:40
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class RunTaskParserAndStatementTest extends BeneratorIntegrationTest {

	@Before
	public void setUpTaskMock() {
		TaskMock.count.set(0);
	}
	
    @Test
	public void testSingleThreaded() throws Exception {
        String xml =
        		"<run-task id='myId' class='org.databene.task.TaskMock' count='5' pageSize='2' stats='true' " +
        		"      pager='new org.databene.task.PageListenerMock(1)'>" +
        		"  <property name='intProp' value='42' />" +
        		"</run-task>";
        RunTaskStatement statement = (RunTaskStatement) parse(xml);
		assertEquals(5L, statement.getCount().evaluate(context).longValue());
		assertEquals(2L, statement.getPageSize().evaluate(context).longValue());
		assertEquals(new PageListenerMock(1), statement.getPager().evaluate(context));
		statement.execute(context);
		assertEquals(5, TaskMock.count.get());
	}
	
    @Test
	public void testMultiThreaded() throws Exception {
        String xml =
        		"<run-task id='myId' class='org.databene.task.TaskMock' count='5' pageSize='2' threads='2' stats='true' " +
        		"      pager='new org.databene.task.PageListenerMock(1)'>" +
        		"  <property name='intProp' value='42' />" +
        		"</run-task>";
        RunTaskStatement statement = (RunTaskStatement) parse(xml);
		assertEquals(5L, statement.getCount().evaluate(context).longValue());
		assertEquals(2L, statement.getPageSize().evaluate(context).longValue());
		assertEquals(new PageListenerMock(1), statement.getPager().evaluate(context));
		statement.execute(context);
		assertEquals(5, TaskMock.count.get());
	}
	
}

/*
 * (c) Copyright 2009-2012 by Volker Bergmann. All rights reserved.
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

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.ErrorHandler;
import org.databene.model.data.Entity;
import org.databene.script.Expression;
import org.databene.script.expression.ConstantExpression;
import org.junit.Test;

/**
 * Tests the {@link GenerateOrIterateStatement}.<br/><br/>
 * Created: 05.11.2009 08:18:17
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class GenerateOrIterateStatementTest extends GeneratorTest {

	private static final int THREAD_COUNT = 30;
	private static final long INVOCATION_COUNT = 6000L;

	@Test
	public void testThreadCount() {
		EntityGenerationStatement entityGenerationStatement = new EntityGenerationStatement();
		
		DefaultBeneratorContext context = new DefaultBeneratorContext();
		GenerateAndConsumeTask task = new GenerateAndConsumeTask("myTask", "myTask");
		task.addStatement(entityGenerationStatement);
		
		Generator<Long> countGenerator = new ConstantGenerator<Long>(INVOCATION_COUNT);
		Expression<Long> pageSize = new ConstantExpression<Long>(300L);
		Expression<Integer> threads = new ConstantExpression<Integer>(THREAD_COUNT);
		Expression<Long> minCount = new ConstantExpression<Long>(INVOCATION_COUNT);
		ConstantExpression<ErrorHandler> errorHandler = new ConstantExpression<ErrorHandler>(ErrorHandler.getDefault());
		GenerateOrIterateStatement statement = new GenerateOrIterateStatement(
				countGenerator, minCount, pageSize, null, threads, errorHandler, true, false, context.createSubContext());
		statement.setTask(task);
		
		statement.execute(context);
		
		assertEquals(INVOCATION_COUNT, entityGenerationStatement.invocationCount);
		int found = entityGenerationStatement.threads.size();
		assertTrue("Exprected at least " + THREAD_COUNT + " threads, but had only " + found, found >= THREAD_COUNT);
	}
	
	// helpers ---------------------------------------------------------------------------------------------------------
	
	class EntityGenerationStatement implements Statement {
		
		public int invocationCount;
		public Set<Thread> threads = new HashSet<Thread>();

		@SuppressWarnings("synthetic-access")
		public boolean execute(BeneratorContext context) {
			int tmp = invocationCount;
			threads.add(Thread.currentThread());
            invocationCount = tmp + 1; 	// update is slightly delayed in order to provoke update errors...
            ProductWrapper<Entity> wrapper = new ProductWrapper<Entity>().wrap(createEntity("Person", "name", "Alice"));
			context.setCurrentProduct(wrapper); // ...in case of concurrency issues
			return true;
		}
	}
	
}

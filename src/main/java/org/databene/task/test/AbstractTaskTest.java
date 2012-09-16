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

package org.databene.task.test;

import org.databene.benerator.test.ModelTest;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Level;
import org.databene.task.Task;
import org.databene.task.TaskResult;

import static org.junit.Assert.*;

/**
 * Parent class for {@link Task} tests.<br/><br/>
 * Created: 10.02.2010 10:11:35
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class AbstractTaskTest extends ModelTest {

	protected static void executeStepAndAssertAvailability(Task task, Context context) {
		assertEquals("Task is expected to be available", TaskResult.EXECUTING, task.execute(context, errorHandler()));
	}
	
	protected static void executeStepAndAssertUnavailability(Task task, Context context) {
		assertTrue("Task is expected to be unavailable", 
				task.execute(context, errorHandler()) != TaskResult.EXECUTING);
	}

	static ErrorHandler errorHandler() {
		return new ErrorHandler(AbstractTaskTest.class.getName(), Level.fatal);
	}
	
}

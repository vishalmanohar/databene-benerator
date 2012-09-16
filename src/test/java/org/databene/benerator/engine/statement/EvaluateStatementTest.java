/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.storage.AbstractStorageSystem;
import org.databene.commons.Context;
import org.databene.commons.Encodings;
import org.databene.commons.SystemInfo;
import org.databene.model.data.Entity;
import org.databene.model.data.TypeDescriptor;
import org.databene.script.Expression;
import org.databene.script.expression.ExpressionUtil;
import org.databene.webdecs.DataSource;
import org.junit.Test;

import static org.databene.script.expression.ExpressionUtil.*;
import static org.junit.Assert.*;

/**
 * Tests the {@link EvaluateStatement}.<br/><br/>
 * Created: 12.02.2010 13:18:42
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class EvaluateStatementTest extends AbstractStatementTest {

	@Test
	public void testInlineJavaScript() {
		EvaluateStatement stmt = new EvaluateStatement(
			true,
			constant("message"),
			constant("'Hello World'"),
			null,
			null,
			null,
			null,
			constant("fatal"),
			constant(Encodings.UTF_8),
			constant(false),
			null,
			null);
		stmt.execute(context);
		assertEquals("Hello World", context.get("message"));
	}
	
	@Test
	public void testUriMapping() {
		EvaluateStatement stmt = new EvaluateStatement(
			true,
			constant("message"),
			null,
			constant("/org/databene/benerator/engine/statement/HelloWorld.js"),
			null,
			null,
			null,
			constant("fatal"),
			constant(Encodings.UTF_8),
			constant(false),
			null,
			null);
		stmt.execute(context);
		assertEquals("Hello World", context.get("message"));
	}
	
	@Test
	public void testShell() {
		String cmd = "echo 42";
		if (SystemInfo.isWindows())
			cmd = "cmd.exe /C " + cmd;
		EvaluateStatement stmt = new EvaluateStatement(
				true,
				constant("result"),
				constant(cmd),
				null,
				constant("shell"),
				null,
				null,
				constant("fatal"),
				constant(Encodings.UTF_8),
				constant(false),
				null,
				null);
			stmt.execute(context);
			assertEquals(42, context.get("result"));
	}
	
	@Test
	public void testStorageSystem() {
		StSys stSys = new StSys();
		Expression<StSys> stSysEx = ExpressionUtil.constant(stSys);
		EvaluateStatement stmt = new EvaluateStatement(
				true,
				constant("message"),
				constant("HelloHi"),
				null,
				null,
				stSysEx,
				null,
				constant("fatal"),
				constant(Encodings.UTF_8),
				constant(false),
				null,
				null);
			stmt.execute(context);
			assertEquals("HelloHi", stSys.execInfo);
	}

	public class StSys extends AbstractStorageSystem {

		protected String execInfo;

		public TypeDescriptor[] getTypeDescriptors() {
			return new TypeDescriptor[0];
		}

		public TypeDescriptor getTypeDescriptor(String typeName) {
			return null;
		}

		public String getId() {
			return "id";
		}

		public DataSource<Entity> queryEntities(String type,
				String selector, Context context) {
			return null;
		}

		public DataSource<?> queryEntityIds(String entityName,
				String selector, Context context) {
			return null;
		}

		public DataSource<?> query(String selector, boolean simplify, Context context) {
			return null;
		}

		public void store(Entity entity) {
		}

		public void update(Entity entity) {
		}

		@Override
		public Object execute(String command) {
			this.execInfo = command;
			return command;
		}

		public void flush() {
		}

		public void close() {
		}

	}

}

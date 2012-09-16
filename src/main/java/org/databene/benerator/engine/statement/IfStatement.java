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

import java.io.Closeable;
import java.io.IOException;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.Statement;
import org.databene.script.Expression;

/**
 * {@link CompositeStatement} that executes it parts 
 * only if a condition is matched.<br/><br/>
 * Created: 19.02.2010 09:13:30
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class IfStatement extends ConditionStatement {
	
	private Statement thenStatement;
	private Statement elseStatement;

	public IfStatement(Expression<Boolean> condition) {
	    super(condition);
    }

	public IfStatement(Expression<Boolean> condition, Statement thenStatement) {
	    this(condition, thenStatement, null);
    }

	public IfStatement(Expression<Boolean> condition, Statement thenStatement, Statement elseStatement) {
	    super(condition);
	    setThenStatement(thenStatement);
	    setElseStatement(elseStatement);
    }

    public boolean execute(BeneratorContext context) {
	    if (condition.evaluate(context))
	    	return thenStatement.execute(context);
	    else if (elseStatement != null)
	    	return elseStatement.execute(context);
	    return true;
    }

	public void setThenStatement(Statement thenStatement) {
	    this.thenStatement = thenStatement;
    }

	public void setElseStatement(Statement elseStatement) {
	    this.elseStatement = elseStatement;
    }

	public void close() throws IOException {
		if (thenStatement instanceof Closeable)
			((Closeable) thenStatement).close();
		if (elseStatement instanceof Closeable)
			((Closeable) elseStatement).close();
	}

}

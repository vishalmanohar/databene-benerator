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

package org.databene.benerator.engine.statement;

import org.databene.benerator.BeneratorError;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.Statement;
import org.databene.script.Expression;
import org.databene.script.expression.ExpressionUtil;

/**
 * {@link Statement} implementation that raises a {@link BeneratorError} 
 * and provides a result <code>code</code> for the operating system.<br/><br/>
 * Created: 12.01.2011 09:04:26
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class ErrorStatement implements Statement {

	public final Expression<String> messageEx;
	public final Expression<Integer> codeEx;

    public ErrorStatement(Expression<String> messageEx, Expression<Integer> codeEx) {
	    this.messageEx = messageEx;
	    this.codeEx = codeEx;
    }

	public boolean execute(BeneratorContext context) {
		String message = ExpressionUtil.evaluate(messageEx, context);
		Integer code = ExpressionUtil.evaluate(codeEx, context);
		if (code == null)
			code = 0;
		throw new BeneratorError(message, code); 
    }

}

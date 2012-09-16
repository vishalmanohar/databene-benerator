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

package org.databene.benerator.engine.statement;

import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.expression.CachedExpression;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Level;
import org.databene.script.Expression;

/**
 * Abstract implementation of the Statement interface.<br/><br/>
 * Created: 27.10.2009 20:16:20
 * @since 0.5.0
 * @author Volker Bergmann
 */
public abstract class AbstractStatement implements Statement {

    private Expression<ErrorHandler> errorHandler;

    // constructors ----------------------------------------------------------------------------------------------------

    protected AbstractStatement() {
        this(null);
    }

    protected AbstractStatement(Expression<ErrorHandler> errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    // Task interface --------------------------------------------------------------------------------------------------

    public ErrorHandler getErrorHandler(Context context) {
    	if (errorHandler == null)
    		return new ErrorHandler(getClass().getName(), Level.fatal);
		return errorHandler.evaluate(context);
	}
    
    // helpers ---------------------------------------------------------------------------------------------------------
    
    protected void handleError(String message, Context context) {
    	getErrorHandler(context).handleError(message);
    }
    
    protected void handleError(String message, Context context, Throwable t) {
    	getErrorHandler(context).handleError(message, t);
    }
    
    protected static <T> Expression<T> cache(Expression<T> expression) {
		return (expression != null ? new CachedExpression<T>(expression) : null);
	}

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}

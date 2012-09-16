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

package org.databene.benerator.util;

import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.script.Expression;

/**
 * Evaluates an {@link Expression} on each call to {@link ExpressionBasedGenerator#generate(ProductWrapper)} 
 * and returns its results.<br/><br/>
 * Created: 27.03.2010 19:51:46
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ExpressionBasedGenerator<E> extends ThreadSafeGenerator<E> {

	private Expression<E> expression;
	private Class<E> generatedType;
	
	public ExpressionBasedGenerator(Expression<E> expression, Class<E> generatedType) {
	    this.expression = expression;
	    this.generatedType = generatedType;
    }

	public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
	    return wrapper.wrap(expression.evaluate(context));
    }

	public Class<E> getGeneratedType() {
	    return generatedType;
    }

	@Override
	public String toString() {
	    return getClass().getSimpleName() + "(" + expression + " -> " + generatedType + ")";
	}

}

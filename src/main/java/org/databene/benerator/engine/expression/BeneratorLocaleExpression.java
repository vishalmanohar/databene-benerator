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

package org.databene.benerator.engine.expression;

import java.util.Locale;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.commons.Context;
import org.databene.script.Expression;
import org.databene.script.expression.ExpressionUtil;

/**
 * Evaluates a string expression for a Locale string, if none is set, it picks Benerator's default Locale.<br/><br/>
 * Created: 29.11.2010 15:30:37
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class BeneratorLocaleExpression implements Expression<Locale> {

	private Expression<String> provider;

	public BeneratorLocaleExpression(Expression<String> codeProvider) {
		this.provider = codeProvider;
	}

	public Locale evaluate(Context context) {
		String localeSpec = ExpressionUtil.evaluate(provider, context);
		return (localeSpec != null ? new Locale(localeSpec) : ((BeneratorContext) context).getDefaultLocale());
	}

	public boolean isConstant() {
		return (provider == null || provider.isConstant());
	}
	
}

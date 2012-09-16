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

package org.databene.benerator.engine.parser;

import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.commons.Context;
import org.databene.commons.ConversionException;
import org.databene.commons.context.ContextAware;
import org.databene.commons.converter.ThreadSafeConverter;
import org.databene.script.ScriptUtil;

/**
 * Parses a string and interprets it as a {@link Distribution} spec, 
 * supporting the predefined sequences, like 'random' and 'cumulated'.<br/><br/>
 * Created: 04.05.2010 06:43:01
 * @since 0.6.1
 * @author Volker Bergmann
 */
public class String2DistributionConverter extends ThreadSafeConverter<String, Distribution> implements ContextAware {
	
	private Context context;

	public String2DistributionConverter() {
	    super(String.class, Distribution.class);
    }

	public Distribution convert(String stringOrScript) throws ConversionException {
		Object sourceValue = ScriptUtil.parseUnspecificText(stringOrScript).evaluate(context);
		Distribution result;
		if (sourceValue instanceof String) {
			String spec = (String) sourceValue;
			result = SequenceManager.getRegisteredSequence(spec, false);
			if (result == null)
				result = (Distribution) context.get(spec);
		} else
			result = (Distribution) sourceValue;
	    return result;
    }

	public void setContext(Context context) {
	    this.context = context;
    }

}

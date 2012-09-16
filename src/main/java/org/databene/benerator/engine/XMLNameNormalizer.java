/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine;

import org.databene.commons.Assert;
import org.databene.commons.ConversionException;
import org.databene.commons.StringUtil;
import org.databene.commons.converter.ThreadSafeConverter;

/**
 * Normalizes XML-valid names to Java-valid camel-case names, 
 * e.g. default-script -> defaultScript.<br/><br/>
 * Created: 26.10.2009 09:17:53
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class XMLNameNormalizer extends ThreadSafeConverter<String, String> {

	public XMLNameNormalizer() {
	    super(String.class, String.class);
    }
	
	public String convert(String name) throws ConversionException {
		return normalize(name);
	}

	public String normalize (String name) {
		Assert.notNull(name, "name");
		String[] tokens = StringUtil.tokenize(name, '-');
		if (tokens.length == 1)
			return name;
		StringBuilder builder = new StringBuilder(tokens[0]);
		for (int i = 1; i < tokens.length; i++)
			builder.append(StringUtil.capitalize(tokens[i]));
		return builder.toString();
	}

}

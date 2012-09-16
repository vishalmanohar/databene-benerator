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

package org.databene.benerator.primitive;

import java.util.Locale;
import java.util.Set;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.wrapper.LengthGenerator;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.regex.RegexParser;

/**
 * Generates {@link String}s composed of numerical digits.<br/><br/>
 * Created: 16.10.2009 07:31:16
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class RandomVarLengthStringGenerator extends LengthGenerator<Character, String> 
		implements VarLengthStringGenerator {
	
	private String pattern;
	private Locale locale;
	private Set<Character> chars;
	
	public RandomVarLengthStringGenerator() {
	    this("[0-9]", 8);
    }

	public RandomVarLengthStringGenerator(String pattern, int length) {
	    this(pattern, length, length, 1);
    }

	public RandomVarLengthStringGenerator(String pattern, int minLength, int maxLength, int lengthGranularity) {
	    this(pattern, minLength, maxLength, lengthGranularity, null);
    }

	public RandomVarLengthStringGenerator(String pattern, int minLength, int maxLength, 
			int lengthGranularity, Distribution lengthDistribution) {
		super(null, true, minLength, maxLength, lengthGranularity, lengthDistribution);
		this.pattern = pattern;
    }
	
	public RandomVarLengthStringGenerator(Set<Character> chars, int minLength, int maxLength, 
			int lengthGranularity, Distribution lengthDistribution) {
		super(null, true, minLength, maxLength, lengthGranularity, lengthDistribution);
		this.chars = chars;
    }
	
	
	
	// properties ------------------------------------------------------------------------------------------------------

	public String getPattern() {
		return pattern;
	}
	
	public void setPattern(String charSet) {
		this.pattern = charSet;
		this.chars = null;
	}
	
	public Locale getLocale() {
		return locale;
	}
	
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	public Set<Character> getChars() {
		return chars;
	}
	
	public void setChars(Set<Character> chars) {
		this.chars = chars;
		this.pattern = null;
	}
	
	
	
	// Generator interface implementation ------------------------------------------------------------------------------

	public Class<String> getGeneratedType() {
	    return String.class;
    }
	
	@Override
	public void init(GeneratorContext context) {
		if (pattern != null) {
	        Object regex = new RegexParser(locale).parseSingleChar(pattern);
	        this.chars = RegexParser.toSet(regex);
		}
		setSource(new CharacterGenerator(chars));
		super.init(context);
	}
	
	public ProductWrapper<String> generate(ProductWrapper<String> wrapper) {
		return wrapper.wrap(generate());
	}

	public String generate() {
		return generateWithLength(generateCardinal());
    }

	public String generateWithLength(int length) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++)
			builder.append(generateFromSource().unwrap());
	    return builder.toString();
    }

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[chars=" + chars + ", minLength=" + getMinLength() + ", " +
				"maxLength=" + getMaxLength() + ", lengthGranularity=" + getLengthGranularity() + ", " +
				"lengthDistribution=" + getLengthDistribution() + "]";
	}
	
}

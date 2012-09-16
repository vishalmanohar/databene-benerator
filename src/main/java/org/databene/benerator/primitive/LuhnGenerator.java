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

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.util.LuhnUtil;
import org.databene.benerator.wrapper.NonNullGeneratorProxy;
import org.databene.commons.StringUtil;

/**
 * Generates numbers that pass a Luhn test.<br/><br/>
 * Created: 18.10.2009 10:08:09
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class LuhnGenerator extends NonNullGeneratorProxy<String> {
	
	protected String prefix;
	protected int minLength;
	protected int maxLength;
	protected int lengthGranularity;
	protected Distribution lengthDistribution;
	
	public LuhnGenerator() {
	    this("", 16);
    }
	
	public LuhnGenerator(String prefix, int length) {
	    this(prefix, length, length, 1, null);
    }

	public LuhnGenerator(String prefix, int minLength, int maxLength, int lengthGranularity, Distribution lengthDistribution) {
	    super(String.class);
	    this.prefix = prefix;
	    this.minLength = minLength;
	    this.maxLength = maxLength;
	    this.lengthGranularity = 1;
	    this.lengthDistribution = lengthDistribution;
    }

	public void setPrefix(String prefix) {
    	this.prefix = prefix;
    }

	public void setMinLength(int minLength) {
    	this.minLength = minLength;
    }

	public void setMaxLength(int maxLength) {
    	this.maxLength = maxLength;
    }

	@Override
	public synchronized void init(GeneratorContext context) {
		super.setSource(new RandomVarLengthStringGenerator("\\d", minLength, maxLength, lengthGranularity, lengthDistribution));
	    super.init(context);
	}
	
	@Override
    public String generate() throws IllegalGeneratorStateException {
		String number = super.generate();
		if (!StringUtil.isEmpty(prefix))
			number = prefix + number.substring(prefix.length());
		char checkDigit = LuhnUtil.requiredCheckDigit(number);
		if (StringUtil.lastChar(number) == checkDigit)
			return number;
		else
			return number.substring(0, number.length() - 1) + checkDigit;
    }

}

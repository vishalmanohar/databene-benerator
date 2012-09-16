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

package org.databene.domain.person;

import java.io.IOException;
import java.util.Locale;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.sample.NonNullSampleGenerator;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Converter;
import org.databene.commons.ThreadAware;
import org.databene.commons.converter.CaseConverter;
import org.databene.commons.converter.ConverterChain;
import org.databene.domain.net.DomainGenerator;
import org.databene.text.DelocalizingConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates email addresses for a random domain by a given person name.<br/><br/>
 * Created: 22.02.2010 12:16:11
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class EMailAddressBuilder implements ThreadAware {
	
	private Logger logger = LoggerFactory.getLogger(getClass()); // Logger is not static in order to adopt sub classes
	
	private DomainGenerator domainGenerator;
	private CaseConverter caseConverter;  
	private Converter<String, String> nameConverter;
	private NonNullSampleGenerator<Character> joinGenerator;
	
	// constructor -----------------------------------------------------------------------------------------------------

	public EMailAddressBuilder(String dataset) {
		logger.debug("Creating instance of {} for dataset {}", getClass(), dataset);
		this.domainGenerator = new DomainGenerator(dataset);
		this.caseConverter = new CaseConverter(false);
		try {
			this.nameConverter = new ConverterChain<String, String>(
					new DelocalizingConverter(),
					caseConverter);
		} catch (IOException e) {
			throw new ConfigurationError("Error in Converter setup", e);
		}
		this.joinGenerator = new NonNullSampleGenerator<Character>(Character.class, '_', '.', '0', '1');
    }
	
	// properties ------------------------------------------------------------------------------------------------------

	public void setDataset(String datasetName) {
		domainGenerator.setDataset(datasetName);
	}
	
	public void setLocale(Locale locale) {
		caseConverter.setLocale(locale);
	}
	
	// generator-like interface ----------------------------------------------------------------------------------------
	
	public void init(GeneratorContext context) {
		domainGenerator.init(context);
		joinGenerator.init(context);
	}
	
	public String generate(String givenName, String familyName) {
		String given = nameConverter.convert(givenName);
		String family = nameConverter.convert(familyName);
		String domain = domainGenerator.generate();
		Character join = joinGenerator.generate();
		switch (join) {
			case '.' : return given + '.' + family + '@' + domain;
			case '_' : return given + '_' + family + '@' + domain;
			case '0' : return given + family + '@' + domain;
			case '1' : return given.charAt(0) + family + '@' + domain;
			default  : throw new ConfigurationError("Invalid join strategy: " + join);
		}
    } 
	
	// ThreadAware interface implementation ----------------------------------------------------------------------------
	
	public boolean isParallelizable() {
		return domainGenerator.isParallelizable() 
			&& caseConverter.isParallelizable() 
			&& nameConverter.isParallelizable() 
			&& joinGenerator.isParallelizable();
    }

	public boolean isThreadSafe() {
		return domainGenerator.isThreadSafe() 
			&& caseConverter.isThreadSafe() 
			&& nameConverter.isThreadSafe() 
			&& joinGenerator.isThreadSafe();
    }
	
	// java.lang.Object overrides --------------------------------------------------------------------------------------
	
	@Override
	public String toString() {
	    return BeanUtil.toString(this);
	}

}

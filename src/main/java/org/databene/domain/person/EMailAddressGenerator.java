/*
 * (c) Copyright 2007-2011 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
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

import java.util.Locale;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.LocaleUtil;
import org.databene.domain.address.Country;

/**
 * Generates EMail Addresses.<br/><br/>
 * Created at 09.04.2008 01:34:17
 * @since 0.5.1
 * @author Volker Bergmann
 */
public class EMailAddressGenerator extends EMailAddressBuilder implements NonNullGenerator<String> { 
	
	private PersonGenerator personGenerator;
	
	public EMailAddressGenerator() {
		this(Country.getDefault().getIsoCode());
	}
	
	public EMailAddressGenerator(String dataset) {
		super(dataset); // creation log is done in parent class constructor
		this.personGenerator = new PersonGenerator(dataset, LocaleUtil.getFallbackLocale());
	}
	
	// properties ------------------------------------------------------------------------------------------------------
	
	@Override
    public void setDataset(String datasetName) {
		super.setDataset(datasetName);
		personGenerator.setDataset(datasetName);
	}
	
	@Override
    public void setLocale(Locale locale) {
		super.setLocale(locale);
		personGenerator.setLocale(locale);
	}
	
	// Generator interface ---------------------------------------------------------------------------------------------
	
	public Class<String> getGeneratedType() {
	    return String.class;
    }

	@Override
    public void init(GeneratorContext context) throws InvalidGeneratorSetupException {
		personGenerator.init(context);
		super.init(context);
    }
	
	public ProductWrapper<String> generate(ProductWrapper<String> wrapper) {
		return wrapper.wrap(generate());
	}

	public String generate() {
		Person person = personGenerator.generate();
		return generate(person.getGivenName(), person.getFamilyName());
	}
	
	public boolean wasInitialized() {
	    return personGenerator.wasInitialized();
	}
	
	public void reset() {
    }

	public void close() {
    }

	// ThreadAware interface implementation ----------------------------------------------------------------------------

	@Override
	public boolean isThreadSafe() {
	    return super.isThreadSafe() && personGenerator.isThreadSafe();
	}

	@Override
	public boolean isParallelizable() {
	    return super.isParallelizable() && personGenerator.isParallelizable();
	}
	
	// java.lang.Object override ---------------------------------------------------------------------------------------
	
	@Override
	public String toString() {
	    return getClass().getName() + '[' + personGenerator.getDataset() + ']';
	}

}

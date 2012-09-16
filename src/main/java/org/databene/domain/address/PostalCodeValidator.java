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

package org.databene.domain.address;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidatorContext;

import org.databene.commons.ConfigurationError;
import org.databene.commons.Encodings;
import org.databene.commons.IOUtil;
import org.databene.commons.Validator;
import org.databene.commons.validator.bean.AbstractConstraintValidator;

/**
 * {@link Validator} that verifies postal codes.<br/><br/>
 * Created: 28.08.2010 15:27:35
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class PostalCodeValidator extends AbstractConstraintValidator<PostalCode, String> {

	Pattern pattern;
	
	public PostalCodeValidator() {
		this(Country.getDefault().getIsoCode());
	}

	public PostalCodeValidator(String countryCode) {
		setCountry(countryCode);
	}

	@Override
	public void initialize(PostalCode params) {
        setCountry(params.country());
	}

	private void setCountry(String countryCode) {
		try {
	        Map<String, String> formats = IOUtil.readProperties("/org/databene/domain/address/postalCodeFormat.properties", Encodings.UTF_8);
	        pattern = Pattern.compile(formats.get(countryCode));
        } catch (IOException e) {
	        throw new ConfigurationError("Error initializing " + getClass().getSimpleName() + 
	        		" with country code '" + countryCode + "'");
        }
    }

	public boolean isValid(String candidate, ConstraintValidatorContext context) {
	    return (candidate != null ? pattern.matcher(candidate).matches() : false);
    }
	
}

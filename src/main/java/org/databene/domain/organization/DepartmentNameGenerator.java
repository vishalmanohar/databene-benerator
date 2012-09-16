/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.domain.organization;

import java.util.Locale;

import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.sample.WeightedCSVSampleGenerator;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.commons.Encodings;
import org.databene.commons.LocaleUtil;

/**
 * Creates random department names based on a {@link Locale}-specific CSV file. 
 * If not CSV file is found for the requested Locale, the generator falls back 
 * to English.<br/><br/>
 * 
 * Created at 11.07.2009 18:43:55
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class DepartmentNameGenerator extends WeightedCSVSampleGenerator<String> implements NonNullGenerator<String> { 
	
    private static final String FILENAME_PREFIX = "/org/databene/domain/organization/department";

    public DepartmentNameGenerator() {
        this(Locale.getDefault());
    }

    public DepartmentNameGenerator(Locale locale) {
        super(uriForLocale(locale), Encodings.UTF_8);
    }
    
    // properties ------------------------------------------------------------------------------------------------------

    public void setLocale(Locale locale) {
    	setUri(uriForLocale(locale));
    }
    
    // Generator interface implementation ------------------------------------------------------------------------------
    
    @Override
    public Class<String> getGeneratedType() {
        return String.class;
    }
    
	public String generate() {
		return GeneratorUtil.generateNonNull(this);
	}
    
    // helpers ---------------------------------------------------------------------------------------------------------

	private static String uriForLocale(Locale locale) {
		return LocaleUtil.availableLocaleUrl(FILENAME_PREFIX, locale, ".csv");
	}

}

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

import java.util.Locale;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.csv.LocalCSVGenerator;
import org.databene.benerator.util.RandomUtil;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.Encodings;

/**
 * Creates nobility titles at a defined quota.
 * Titles are defined in the files 'org/databene/domain/person/nobTitle_*_*.csv'.
 * See the Wikipedia articles on <a href="http://en.wikipedia.org/wiki/Royal_and_noble_ranks">Royal 
 * and noble ranks</a> and <a href="http://en.wikipedia.org/wiki/Nobility">Nobility</a> for further 
 * information on the domain.<br/><br/>
 * Created: 11.02.2010 12:04:01
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class NobilityTitleGenerator extends GeneratorProxy<String> {
	
	private final static String BASE_NAME = "/org/databene/domain/person/nobTitle_";
	
	private Gender gender;
	private Locale locale;
	private float nobleQuota = 0.005f;

    public NobilityTitleGenerator() {
        this(Gender.MALE, Locale.getDefault());
    }

    public NobilityTitleGenerator(Gender gender, Locale locale) {
        super(String.class);
        this.gender = gender;
        this.locale = locale;
    }
    
    // properties ------------------------------------------------------------------------------------------------------
    
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	
	public Locale getLocale() {
		return locale;
	}
	
    public void setLocale(Locale locale) {
    	this.locale = locale;
    }
    
    public double getNobleQuota() {
	    return nobleQuota;
    }

    public void setNobleQuota(double nobleQuota) {
	    this.nobleQuota = (float) nobleQuota;
    }

    // Generator interface implementation ------------------------------------------------------------------------------
    
    @Override
    public ProductWrapper<String> generate(ProductWrapper<String> wrapper) {
        if (RandomUtil.randomProbability() < getNobleQuota())
        	return super.generate(wrapper);
        else
        	return wrapper.wrap("");
    }
    
    @Override
    public synchronized void init(GeneratorContext context) {
    	setSource(createCSVGenerator(gender, locale));
    	super.init(context);
    }
    
    // helper methods --------------------------------------------------------------------------------------------------

	private static LocalCSVGenerator<String> createCSVGenerator(Gender gender, Locale locale) {
	    return new LocalCSVGenerator<String>(String.class, baseName(gender), locale, ".csv", Encodings.UTF_8);
    }

    private static String baseName(Gender gender) {
        if (gender == Gender.FEMALE)
            return BASE_NAME + "female";
        else if (gender == Gender.MALE)
            return BASE_NAME + "male";
        else
            throw new IllegalArgumentException("Gender: " + gender);
    }

}

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

package org.databene.domain.person;

import java.util.Locale;
import java.util.Set;

import org.databene.commons.CollectionUtil;
import org.databene.commons.ConversionException;
import org.databene.commons.LocaleUtil;
import org.databene.commons.Locales;
import org.databene.commons.StringUtil;
import org.databene.commons.converter.ThreadSafeConverter;

/**
 * Formats {@link Person} objects.<br/><br/>
 * Created: 22.02.2010 12:41:37
 * @since 0.6.0
 * @author Volker Bergmann
 */
public abstract class PersonFormatter extends ThreadSafeConverter<Person, String> {
	
	private static final Set<Locale> EASTERN_LOCALES = CollectionUtil.toSet(
		Locales.CHINESE, Locales.JAPANESE, Locales.KOREAN, Locales.THAI, Locales.VIETNAMESE
	);
	
	public static final PersonFormatter WESTERN = new Western();
	public static final PersonFormatter EASTERN = new Eastern();

	public PersonFormatter() {
	    super(Person.class, String.class);
    }

	public String convert(Person person) throws ConversionException {
		return format(person);
    }

	public abstract String format(Person person);

	public static PersonFormatter getInstance(Locale locale) {
	    return (EASTERN_LOCALES.contains(LocaleUtil.language(locale)) ? EASTERN : WESTERN);
    }

	protected void appendSeparated(String part, StringBuilder builder) {
		if (!StringUtil.isEmpty(part)) {
			if (builder.length() > 0)
				builder.append(' ');
			builder.append(part);
		}
    }

	static class Western extends PersonFormatter {
		
		@Override
        public String format(Person person) {
		    StringBuilder builder = new StringBuilder();
		    appendSeparated(person.getSalutation(), builder);
			appendSeparated(person.getAcademicTitle(), builder);
			appendSeparated(person.getNobilityTitle(), builder);
			appendSeparated(person.getGivenName(), builder);
			appendSeparated(person.getFamilyName(), builder);
			return builder.toString();
	    }
	}
	
	static class Eastern extends PersonFormatter {
		
		@Override
        public String format(Person person) {
		    StringBuilder builder = new StringBuilder();
		    appendSeparated(person.getSalutation(), builder);
			appendSeparated(person.getAcademicTitle(), builder);
			appendSeparated(person.getNobilityTitle(), builder);
			appendSeparated(person.getFamilyName(), builder);
			appendSeparated(person.getGivenName(), builder);
			return builder.toString();
	    }
	}
	
}

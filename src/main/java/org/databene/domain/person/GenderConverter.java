/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

import org.databene.commons.ConversionException;
import org.databene.commons.converter.ThreadSafeConverter;

/**
 * Converts a {@link Gender} enumeration value to a configurable String.<br/>
 * <br/>
 * Created at 11.03.2009 12:31:43
 * @since 0.5.8
 * @author Volker Bergmann
 */

public class GenderConverter extends ThreadSafeConverter<Gender, String> {
	
	private String male;
	private String female;

    public GenderConverter() {
	    this("m", "f");
    }

    public GenderConverter(String male, String female) {
	    super(Gender.class, String.class);
	    this.male = male;
	    this.female = female;
    }

	public void setMale(String male) {
    	this.male = male;
    }

	public void setFemale(String female) {
    	this.female = female;
    }

	public String convert(Gender gender) throws ConversionException {
	    return (gender != null ? (Gender.MALE.equals(gender) ? male : female) : null);
    }
	
}

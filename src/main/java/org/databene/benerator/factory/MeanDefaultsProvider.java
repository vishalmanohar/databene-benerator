/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.factory;

import java.util.Date;

import org.databene.commons.NumberUtil;
import org.databene.commons.TimeUtil;
import org.databene.commons.converter.NumberToNumberConverter;

/**
 * {@link DefaultsProvider} implementation which provides mean defaults 
 * for provoking errors in functional testing.<br/><br/>
 * Created: 15.07.2011 21:22:39
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class MeanDefaultsProvider implements DefaultsProvider {

	public <T extends Number> T defaultMin(Class<T> numberType) {
		return NumberUtil.minValue(numberType); 
	}

	public <T extends Number> T defaultMax(Class<T> numberType) {
		return NumberUtil.maxValue(numberType);
	}

	public <T extends Number> T defaultGranularity(Class<T> numberType) {
		return NumberToNumberConverter.convert(1, numberType);
	}

	public int defaultMinLength() {
		return 0;
	}

	public Integer defaultMaxLength() {
		return 1000;
	}

	public boolean defaultNullable() {
		return true;
	}

	public double defaultNullQuota() {
		return 0.5;
	}

	public Date defaultMinDate() {
		return TimeUtil.date(-2000, 0, 1);
	}

	public Date defaultMaxDate() {
		return TimeUtil.date(2999, 11, 31);
	}

}

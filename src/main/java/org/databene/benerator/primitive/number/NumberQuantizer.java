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

package org.databene.benerator.primitive.number;

import org.databene.commons.ConversionException;
import org.databene.commons.converter.NumberToNumberConverter;
import org.databene.commons.converter.ThreadSafeConverter;
import org.databene.script.math.ArithmeticEngine;

/**
 * A quantizer for arbitrary number types.<br/><br/>
 * Created: 05.07.2011 08:19:20
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class NumberQuantizer<E extends Number> extends ThreadSafeConverter<Number, E> {

	private E min;
	private E granularity;
	private Class<E> numberType;

	public NumberQuantizer(E min, E granularity, Class<E> numberType) {
	    super(Number.class, numberType);
	    this.min = min;
	    this.granularity = granularity;
	    this.numberType = numberType;
    }

	public E convert(Number sourceValue) throws ConversionException {
		return quantize(sourceValue, min, granularity, numberType);
    }

	public static <T extends Number> T quantize(Number sourceValue, T min, T granularity, Class<T> numberType) throws ConversionException {
		T value = NumberToNumberConverter.convert(sourceValue, numberType);
		ArithmeticEngine engine = ArithmeticEngine.defaultInstance();
		long ofs = ((Number) engine.divide(engine.subtract(value, min), granularity)).longValue();
		return NumberToNumberConverter.convert((Number) engine.add(min, engine.multiply(ofs, granularity)), numberType);
    }

}

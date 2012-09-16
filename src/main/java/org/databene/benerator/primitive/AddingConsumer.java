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

package org.databene.benerator.primitive;

import org.databene.benerator.Consumer;
import org.databene.benerator.consumer.AbstractConsumer;
import org.databene.commons.Accessor;
import org.databene.commons.Converter;
import org.databene.commons.StringUtil;
import org.databene.commons.accessor.FeatureAccessor;
import org.databene.commons.converter.NumberToNumberConverter;
import org.databene.script.PrimitiveType;
import org.databene.script.math.ArithmeticEngine;

/**
 * {@link Consumer} implementation which sums up the values of a 'feature' of all objects it consumes
 * and return the sum as 'sum' property of type 'type'.<br/><br/>
 * Created: 03.04.2010 07:41:42
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class AddingConsumer extends AbstractConsumer {

	private Accessor<Object, Number> accessor;
	
	private Class<? extends Number> numberType;
	
    private Converter<Number, ? extends Number> converter;
	
	private Number sum;
	
	public AddingConsumer() {
		this(null, null);
	}
	
	public AddingConsumer(String feature, String type) {
	    setFeature(feature);
	    setType(type);
    }

	public void setFeature(String feature) {
		this.accessor = (feature != null ? new FeatureAccessor<Object, Number>(feature, true) : null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public void setType(String typeName) {
		if (StringUtil.isEmpty(typeName))
			typeName = "double";
		this.numberType = (Class<? extends Number>) PrimitiveType.getInstance(typeName).getJavaType();
		this.converter = new NumberToNumberConverter(Number.class, numberType);
		this.sum = converter.convert(0);
	}
	
	public Number getSum() {
		return this.sum;
	}
	
	@Override
	public void startProductConsumption(Object object) {
	    Number addend = converter.convert(accessor.getValue(object));
	    this.sum = (Number) ArithmeticEngine.defaultInstance().add(sum, addend);
    }

}

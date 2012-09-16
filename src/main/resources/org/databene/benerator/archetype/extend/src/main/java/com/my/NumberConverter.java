package com.my;

import java.text.DecimalFormat;

import org.databene.commons.ConversionException;
import org.databene.commons.converter.SimpleConverter;

public class NumberConverter extends SimpleConverter<Long, String> {

	private DecimalFormat format;
	
	public NumberConverter(String pattern) {
		super(Long.class, String.class);
		this.format = new DecimalFormat(pattern);
	}

	public String convert(Long source) throws ConversionException {
		return format.format(source);
	}
	
}

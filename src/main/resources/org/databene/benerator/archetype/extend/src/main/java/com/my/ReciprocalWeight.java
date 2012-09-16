package com.my;

import org.databene.benerator.distribution.AbstractWeightFunction;

public class ReciprocalWeight extends AbstractWeightFunction{

	public double value(double x) {
		return 100 / (x + 1);
	}

}

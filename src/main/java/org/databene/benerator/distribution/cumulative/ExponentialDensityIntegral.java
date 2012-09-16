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

package org.databene.benerator.distribution.cumulative;

import org.databene.benerator.distribution.CumulativeDistributionFunction;

/**
 * Inverse of the integral of the probability density f(x) = a e^{-ax} (x > 0), 
 * which resolves to F^{-1}(x) = - log(1 - x) / a.
 * See <a href="http://www.stat.wisc.edu/~larget/math496/random2.html">Random 
 * Number Generation from Non-uniform Distributions</a>.<br/><br/>
 * Created: 12.03.2010 15:41:21
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ExponentialDensityIntegral extends CumulativeDistributionFunction {

	private double a;
	
	public ExponentialDensityIntegral(double a) {
		if (a <= 0)
			throw new IllegalArgumentException("a must be greater than zero, but is " + a);
	    this.a = a;
    }

	@Override
    public double cumulativeProbability(double value) {
	    return 1 - Math.exp(-a * value);
    }

	@Override
	public double inverse(double probability) {
		return - Math.log(1 - probability) / a;
	}

}

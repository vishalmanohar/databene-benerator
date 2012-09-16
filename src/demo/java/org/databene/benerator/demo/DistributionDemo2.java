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

package org.databene.benerator.demo;

import static org.databene.benerator.util.GeneratorUtil.*;

import org.databene.benerator.Generator;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.function.ExponentialFunction;

/**
 * Demonstrates usage of the {@link ExponentialFunction}.<br/><br/>
 * Created: 08.03.2011 17:29:29
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class DistributionDemo2 {

	public static void main(String[] args) {
		generateNumbers();
	}

	private static void generateNumbers() {
		System.out.println("Generating numbers");
		Distribution distribution = new ExponentialFunction(5, -0.5);
		Generator<Double> generator = distribution.createNumberGenerator(Double.class, 3., 8., 0.1, false);
		init(generator);
		for (int i = 0; i < 20; i++)
			System.out.println(generateNonNull(generator));
		close(generator);
	}

}

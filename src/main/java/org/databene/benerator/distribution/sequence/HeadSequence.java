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

package org.databene.benerator.distribution.sequence;

import org.databene.benerator.Generator;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.wrapper.NShotGeneratorProxy;
import org.databene.benerator.wrapper.WrapperFactory;

/**
 * Sequence implementation that returns the first n values of another Generator (default 1).
 * When used to create number generators, it creates generators that count incrementally 
 * from 'min' to min + n - 1.<br/><br/>
 * Created: 25.07.2010 09:55:54
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class HeadSequence extends Sequence {
	
	private static final StepSequence STEP_SEQ = new StepSequence();

	long size;
	
	public HeadSequence() {
	    this(1);
    }

	public HeadSequence(long size) {
	    this.size = size;
    }

	public void setSize(long n) {
		this.size = n;
	}
	
	@Override
	public <T> Generator<T> applyTo(Generator<T> source, boolean unique) {
	    return new NShotGeneratorProxy<T>(source, size);
	}
	
    public <T extends Number> NonNullGenerator<T> createNumberGenerator(
    		Class<T> numberType, T min, T max, T granularity, boolean unique) {
    	Generator<T> source = STEP_SEQ.createNumberGenerator(numberType, min, max, granularity, unique);
		return WrapperFactory.asNonNullGenerator(new NShotGeneratorProxy<T>(source, size));
	}

}

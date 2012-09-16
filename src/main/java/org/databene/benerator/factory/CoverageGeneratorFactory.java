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
import java.util.Set;
import java.util.TreeSet;

import org.databene.benerator.Generator;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.wrapper.GeneratorChain;
import org.databene.benerator.wrapper.WrapperFactory;
import org.databene.model.data.Uniqueness;

/**
 * {@link GeneratorFactory} implementation which creates data sets 
 * that cover the full range of available data and combinations.<br/><br/>
 * Created: 04.07.2011 09:39:08
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class CoverageGeneratorFactory extends EquivalenceGeneratorFactory {
	
	private SerialGeneratorFactory serialFactory;
	
	public CoverageGeneratorFactory() {
		this.serialFactory = new SerialGeneratorFactory();
	}
	
	// generator factory method ----------------------------------------------------------------------------------------
	
    @SuppressWarnings("unchecked")
	@Override
	public Generator<Date> createDateGenerator(
            Date min, Date max, long granularity, Distribution distribution) {
    	return new GeneratorChain<Date>(Date.class, true, 
    		super.createDateGenerator(min, max, granularity, distribution),
    		serialFactory.createDateGenerator(min, max, granularity, distribution)
		);
    }

    @SuppressWarnings("unchecked")
	@Override
	public <T extends Number> NonNullGenerator<T> createNumberGenerator(
            Class<T> numberType, T min, Boolean minInclusive, T max, Boolean maxInclusive, 
            T granularity, Distribution distribution, Uniqueness uniqueness) {
    	return WrapperFactory.asNonNullGenerator(new GeneratorChain<T>(numberType, true, 
    		super.createNumberGenerator(numberType, min, minInclusive, max, maxInclusive, 
    				granularity, distribution, uniqueness),
    		serialFactory.createNumberGenerator(numberType, min, minInclusive, max, maxInclusive, 
    				granularity, distribution, uniqueness)
		));
    }
    
	@SuppressWarnings("unchecked")
	@Override
	public NonNullGenerator<String> createStringGenerator(Set<Character> chars,
			Integer minLength, Integer maxLength, int lengthGranularity, Distribution lengthDistribution, 
			Uniqueness uniqueness) {
    	NonNullGenerator<String> eqGenerator = super.createStringGenerator(
    			chars, minLength, maxLength, lengthGranularity, lengthDistribution, uniqueness);
		NonNullGenerator<String> serialGenerator = serialFactory.createStringGenerator(
				chars, minLength, maxLength, lengthGranularity, lengthDistribution, uniqueness);
		return WrapperFactory.asNonNullGenerator(new GeneratorChain<String>(
				String.class, true, eqGenerator, serialGenerator));
	}
	
	
	
	// defaults --------------------------------------------------------------------------------------------------------
	
    @Override
	public Set<Character> defaultSubSet(Set<Character> characters) {
    	return characters;
    }

	@Override
	protected Set<Integer> defaultCounts(int minParts, int maxParts, int partsGranularity) {
		TreeSet<Integer> counts = new TreeSet<Integer>();
		for (int i = minParts; i <= maxParts; i += partsGranularity)
			counts.add(i);
		return counts;
	}

	@Override
	public Distribution defaultDistribution(Uniqueness uniqueness) {
    	return SequenceManager.STEP_SEQUENCE;
	}

}

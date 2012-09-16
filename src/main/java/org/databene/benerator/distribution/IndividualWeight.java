/*
 * (c) Copyright 2008-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.distribution;

import org.databene.benerator.Generator;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.sample.IndividualWeightSampleGenerator;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.commons.ConfigurationError;

/**
 * Distribution type that provides an individual weight for each object.<br/>
 * <br/>
 * Created at 27.04.2008 19:17:38
 * @since 0.5.2
 * @author Volker Bergmann
 */
public abstract class IndividualWeight<E> implements Weight {
	
	public abstract double weight(E object);
	
    public <T extends Number> NonNullGenerator<T> createNumberGenerator(
    		Class<T> numberType, T min, T max, T granularity, boolean unique) {
	    throw new UnsupportedOperationException("createGenerator() is not supported by " + getClass());
    }

    @SuppressWarnings("unchecked")
    public <T> Generator<T> applyTo(Generator<T> source, boolean unique) {
    	if (unique)
    		throw new ConfigurationError("Uniqueness is not supported by " + getClass());
    	return new IndividualWeightSampleGenerator<T>(source.getGeneratedType(), (IndividualWeight<T>) this, 
    			GeneratorUtil.allProducts(source));
    }

}

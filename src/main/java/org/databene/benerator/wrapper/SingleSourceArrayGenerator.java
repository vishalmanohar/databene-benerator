/*
 * (c) Copyright 2006-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.wrapper;

import java.lang.reflect.Array;

import org.databene.benerator.Generator;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.commons.ArrayUtil;

/**
 * Assembles the output of a source generator into an array of random length.<br/>
 * <br/>
 * Created: 26.08.2006 09:37:55
 * @since 0.1
 * @author Volker Bergmann
 */
public class SingleSourceArrayGenerator<S, P> extends CardinalGenerator<S, P> implements NonNullGenerator<P> {

    private Class<S> componentType;
    private Class<P> generatedType;

    @SuppressWarnings("unchecked")
	public SingleSourceArrayGenerator(Generator<S> source, Class<S> componentType,  
    		int minLength, int maxLength, Distribution lengthDistribution) {
        super(source, false, minLength, maxLength, 1, SequenceManager.RANDOM_SEQUENCE);
        this.componentType = componentType;
        this.generatedType = ArrayUtil.arrayType(componentType);
    }

    @SuppressWarnings("unchecked")
	public SingleSourceArrayGenerator(Generator<S> source, Class<S> componentType, 
			NonNullGenerator<Integer> lengthGenerator) {
        super(source, false, lengthGenerator);
        this.componentType = componentType;
        this.generatedType = ArrayUtil.arrayType(componentType);
    }

    // configuration properties ----------------------------------------------------------------------------------------

    public Class<P> getGeneratedType() {
        return generatedType;
    }

	public ProductWrapper<P> generate(ProductWrapper<P> wrapper) {
        return wrapper.wrap(generate());
    }

	public P generate() {
    	Integer size = generateCardinal();
    	if (size == null)
    		return null;
    	// the following works for primitive types as well as for objects
        @SuppressWarnings("unchecked")
		P array = (P) ArrayUtil.newInstance(componentType, size.intValue());
        for (int i = 0; i < size; i++) {
            ProductWrapper<S> component = generateFromSource();
            if (component == null)
            	return null;
			Array.set(array, i, component.unwrap());
        } 
        return array;
	}

}

/*
 * (c) Copyright 2007-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.util;

import java.util.ArrayList;
import java.util.List;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.engine.BeneratorOpts;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.benerator.wrapper.GeneratorWrapper;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.IOUtil;
import org.databene.commons.Resettable;
import org.databene.commons.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides utility methods for data generation.<br/><br/>
 * Created: 19.11.2007 15:27:50
 * @author Volker Bergmann
 */
public class GeneratorUtil {
	
	private static Logger LOGGER = LoggerFactory.getLogger(GeneratorUtil.class);
	
	public static boolean isBeneratorFile(String localFilename) {
		if (StringUtil.isEmpty(localFilename))
			return false;
		String lcFilename = localFilename.toLowerCase();
		return "benerator.xml".equals(lcFilename) || lcFilename.endsWith(".ben.xml");
	}

    public static void init(Generator<?> generator) {
    	init(generator, new DefaultBeneratorContext());
    }

    public static void init(Generator<?> generator, GeneratorContext context) {
    	generator.init(context);
    }

    public static void close(Generator<?> generator) {
    	IOUtil.close(generator);
    }

    /**
     * Calls a {@link Generator}'s {@link Generator#generate(ProductWrapper)} method and returns its unwrapped result, 
     * allowing <code>null<code> values as generation results, but requiring the generator to be available. 
     */
    public static <T> T generateNullable(Generator<T> generator) {
    	ProductWrapper<T> wrapper = generator.generate(GeneratorUtil.<T>getWrapper());
		if (wrapper == null)
			throw new IllegalGeneratorStateException("Generator unavailable in generateNullable(): " + generator);
		return wrapper.unwrap();
    }

    /**
     * Calls a {@link Generator}'s {@link Generator#generate(ProductWrapper)} method and returns its unwrapped result, 
     * signaling generator unavailability with a <code>null</code> value and requiring the Generator 
     * not to create <code>null</code> values as result. 
     */
    public static <T> T generateNonNull(Generator<T> generator) {
    	ProductWrapper<T> wrapper = generator.generate(GeneratorUtil.<T>getWrapper());
    	if (wrapper == null)
    		return null;
		T result = wrapper.unwrap();
		if (result == null)
			throw new IllegalGeneratorStateException("Generated null value in generateNonNull(): " + generator);
		return result;
    }

	protected static <T> ProductWrapper<T> getWrapper() {
		return new ProductWrapper<T>();
	}

	public static <T> List<T> allProducts(Generator<T> generator) {
		List<T> list = new ArrayList<T>();
		int count = 0;
		int cacheSize = BeneratorOpts.getCacheSize();
		ProductWrapper<T> wrapper = GeneratorUtil.<T>getWrapper();
		while ((wrapper = generator.generate(wrapper)) != null) {
			count++;
			if (count > cacheSize) {
				LOGGER.error("Data set of generator has reached the cache limit and will be reduced to its size " +
						"of " + cacheSize + " elements). " +
						"If that is not acceptable then choose a distribution that does not cache data sets " +
						"or increase the cache size. Concerned generator: " + generator);
				break;
			}
			list.add(wrapper.unwrap());
		}
		return list;
	}
	
    @SuppressWarnings("unchecked")
    public static <T> Class<T> commonTargetTypeOf(Generator<T>... sources) {
    	if (sources.length == 0)
    		return (Class<T>) Object.class;
    	Class<T> type = sources[0].getGeneratedType();
    	for (int i = 1; i < sources.length; i++) {
    		Class<T> tmp = sources[i].getGeneratedType();
    		if (tmp.isAssignableFrom(type))
    			type = tmp;
    	}
		return type;
	}

	public static void initAll(Generator<?>[] generators, GeneratorContext context) {
	    for (Generator<?> generator : generators)
	    	generator.init(context);
    }

	public static void resetAll(Resettable[] resettables) {
	    for (Resettable resettable : resettables)
	    	resettable.reset();
    }

    public static Generator<?> unwrap(Generator<?> generator) {
		Generator<?> result = generator;
		while (result instanceof GeneratorWrapper)
			result = ((GeneratorWrapper<?,?>) result).getSource();
		return result;
	}
	
}

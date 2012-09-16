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

package org.databene.benerator.wrapper;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;

/**
 * Synchronized wrapper class for non-thread-safe {@link Generator} implementations.<br/><br/>
 * Created: 24.02.2010 23:08:39
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class SynchronizedGeneratorProxy<E> implements Generator<E> {
	
	private final Generator<E> source;

	private SynchronizedGeneratorProxy(Generator<E> source) {
	    this.source = source;
    }

	public synchronized void init(GeneratorContext context) {
	    source.init(context);
    }
	
	public synchronized boolean wasInitialized() {
		return source.wasInitialized();
	}

	public synchronized Class<E> getGeneratedType() {
	    return source.getGeneratedType();
    }

	public synchronized ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
	    return source.generate(wrapper);
    }

	public synchronized void reset() {
	    source.reset();
    }

	public synchronized void close() {
	    source.close();
    }

	public boolean isThreadSafe() {
	    return true;
    }

	public boolean isParallelizable() {
	    return false;
    }

}

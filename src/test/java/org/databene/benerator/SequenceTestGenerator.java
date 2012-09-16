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

package org.databene.benerator;

import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.ArrayFormat;

/**
 * Helper class for testing.<br/><br/>
 * Created: 16.12.2006 19:36:25
 * @since 0.1
 * @author Volker Bergmann
 */
public class SequenceTestGenerator<E> implements Generator<E> {

    private E[] sequence;
    int cursor;
    boolean initialized;
    public int generateCount = 0;
    public int resetCount = 0;
    public int closeCount = 0;

    public SequenceTestGenerator(E... sequence) {
        this.sequence = sequence;
        this.cursor = 0;
        this.initialized = false;
    }

    public void init(GeneratorContext context) {
        if (sequence == null)
            throw new IllegalArgumentException("sequence is null");
    }

    @SuppressWarnings("unchecked")
    public Class<E> getGeneratedType() {
        return (Class<E>) (sequence.length > 0 ? sequence[0].getClass() : Object.class);
    }

	public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
    	generateCount++;
        if (cursor >= sequence.length)
            return null;
        return wrapper.wrap(sequence[cursor++]);
    }

    public boolean wasInitialized() {
        return initialized;
    }

    public void reset() {
        this.cursor = 0;
        this.resetCount++;
    }

    public void close() {
    	this.closeCount++;
        this.cursor = sequence.length;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + ArrayFormat.format(sequence) + ']';
    }

	public boolean isParallelizable() {
	    return false;
    }

	public boolean isThreadSafe() {
	    return false;
    }

}

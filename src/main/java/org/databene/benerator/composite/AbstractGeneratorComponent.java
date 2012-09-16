/*
 * (c) Copyright 2011-2012 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.composite;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.engine.AbstractScopedLifeCycleHolder;
import org.databene.benerator.engine.BeneratorContext;

/**
 * Abstract implementation of the GeneratorComponent interface which manages a source Generator
 * and a Context reference.<br/><br/>
 * Created: 31.08.2011 12:56:22
 * @since 0.7.0
 * @author Volker Bergmann
 */
public abstract class AbstractGeneratorComponent<E> extends AbstractScopedLifeCycleHolder implements GeneratorComponent<E> {

	protected Generator<?> source;
	protected GeneratorContext context;
	protected String message;

	public AbstractGeneratorComponent(Generator<?> source, String scope) {
		super(scope);
		this.source = source;
	}

    public Generator<?> getSource() {
    	return source;
    }
    
	public String getMessage() {
		return message;
	}
	
	protected void assertInitialized() {
		if (!source.wasInitialized())
			throw new IllegalGeneratorStateException("Generator component was not initialized: " + this);
	}

    // GeneratorComponent interface implementation ---------------------------------------------------------------------

	public void init(BeneratorContext context) {
		this.context = context;
		source.init(context);
	}

	public void reset() {
		source.reset();
	}
	
	public void close() {
    	source.close();
	}

	public boolean isParallelizable() {
	    return source.isParallelizable();
    }

	public boolean isThreadSafe() {
	    return source.isThreadSafe();
    }
	
	// java.lang.Object overrides --------------------------------------------------------------------------------------

	@Override
	public String toString() {
		return getClass().getSimpleName() + '{' + source + '}';
	}

}

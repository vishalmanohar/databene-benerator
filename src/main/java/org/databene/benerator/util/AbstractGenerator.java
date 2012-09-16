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

package org.databene.benerator.util;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.GeneratorState;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract {@link Generator} implementation which holds a state and state management methods.<br/><br/>
 * Created: 24.02.2010 12:28:05
 * @since 0.6.0
 * @author Volker Bergmann
 */
public abstract class AbstractGenerator<E> implements Generator<E> {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected GeneratorState state;

	protected GeneratorContext context;
	
	private WrapperProvider<E> resultWrapperProvider;

	public AbstractGenerator() {
	    this.state = GeneratorState.CREATED;
	    this.resultWrapperProvider = new WrapperProvider<E>();
    }

	public synchronized void init(GeneratorContext context) {
		this.context = context;
		this.state = GeneratorState.RUNNING;
    }
	
	public boolean wasInitialized() {
	    return (state != GeneratorState.CREATED);
	}

	public void reset() {
	    this.state = GeneratorState.RUNNING;
	}
	
	public void close() {
	    this.state = GeneratorState.CLOSED;
	}
	
	// internal helpers ------------------------------------------------------------------------------------------------
    
    protected final void assertNotInitialized() {
	    if (state != GeneratorState.CREATED)
	    	if (state == GeneratorState.RUNNING)
	    		throw new IllegalGeneratorStateException("Trying to initialize generator a 2nd time: " + this);
	    	else
	    		throw new IllegalGeneratorStateException("Trying to initialize generator in '" + state + "' state: " + this);
    }

    protected final void assertInitialized() {
    	if (state == GeneratorState.CREATED)
    		throw new IllegalGeneratorStateException("Generator has not been initialized: " + this);
    	if (state == GeneratorState.CLOSED)
    		throw new IllegalGeneratorStateException("Generator has already been closed: " + this);
    }
    
    protected ProductWrapper<E> getResultWrapper() {
    	return resultWrapperProvider.get();
    }
    
    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return (state == GeneratorState.RUNNING ? BeanUtil.toString(this) : getClass().getSimpleName());
    }
    
}

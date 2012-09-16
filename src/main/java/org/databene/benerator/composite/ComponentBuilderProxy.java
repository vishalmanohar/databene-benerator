/*
 * (c) Copyright 2010-2012 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.engine.AbstractScopedLifeCycleHolder;
import org.databene.benerator.engine.BeneratorContext;

/**
 * Proxy class for a {@link ComponentBuilder}.<br/><br/>
 * Created: 11.10.2010 11:10:51
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class ComponentBuilderProxy<E> extends AbstractScopedLifeCycleHolder implements ComponentBuilder<E> {
	
	protected ComponentBuilder<E> source;
	protected GeneratorContext context;

	public ComponentBuilderProxy(ComponentBuilder<E> source) {
		super(source.getScope());
	    this.source = source;
    }

	public boolean isParallelizable() {
	    return source.isParallelizable();
    }

	public boolean isThreadSafe() {
	    return source.isThreadSafe();
    }

	public void init(BeneratorContext context) {
		source.init(context);
	}

	public boolean execute(BeneratorContext context) {
	    return source.execute(context);
    }
	
	public void reset() {
	    source.reset();
    }

	public void close() {
	    source.close();
    }

	public String getMessage() {
		return source.getMessage();
	}

}

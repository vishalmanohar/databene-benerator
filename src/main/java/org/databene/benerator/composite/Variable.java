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

package org.databene.benerator.composite;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.wrapper.ProductWrapper;

/**
 * Wraps variable name and generator functionality.<br/><br/>
 * Created: 07.08.2011 16:24:10
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class Variable<E> extends AbstractGeneratorComponent<E> {
	
	private String name;
	
	public Variable(String name, Generator<?> source, String scope) {
		super(source, scope);
		this.name = name;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" }) 
	public boolean execute(BeneratorContext context) {
		assertInitialized(); 
		ProductWrapper<?> productWrapper = source.generate(new ProductWrapper());
		if (productWrapper == null) {
			context.remove(name);
            return false;
		}
        context.set(name, productWrapper.unwrap());
        return true;
	}
	
	// Closeable interface implementation ------------------------------------------------------------------------------
	
	@Override
	public void close() {
		if (context != null) // if the variable has not been used (count="0"), it has not been initialized
			context.remove(name);
		super.close();
	}

	// java.lang.Object overrides --------------------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + name + ":" + source + "]";
	}

}

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
import org.databene.commons.ArrayUtil;

/**
 * Keeps an array of generators, of which it combines the products to an array.<br/><br/>
 * Created: 28.07.2010 19:10:53
 * @since 0.1
 * @author Volker Bergmann
 */
public class MultiSourceArrayGenerator<S> extends GeneratorProxy<S[]> {

	private Class<S> componentType;
    private boolean unique;
    private Generator<? extends S>[] sources;
    
	@SuppressWarnings("unchecked")
	public MultiSourceArrayGenerator(Class<S> componentType, boolean unique, Generator<? extends S>... sources) {
		super(ArrayUtil.arrayType(componentType));
	    this.componentType = componentType;
	    this.unique = unique;
	    this.sources = sources;
    }

	public void setUnique(boolean unique) {
    	this.unique = unique;
    }
	
	public Generator<? extends S>[] getSources() {
		return sources;
	}
	
	public void setSources(Generator<? extends S>[] sources) {
		this.sources = sources;
	}
    
    @Override
    public synchronized void init(GeneratorContext context) {
		if (unique)
			super.setSource(new UniqueMultiSourceArrayGenerator<S>(componentType, sources));
		else
			super.setSource(new SimpleMultiSourceArrayGenerator<S>(componentType, sources));
	    super.init(context);
    }
    
}

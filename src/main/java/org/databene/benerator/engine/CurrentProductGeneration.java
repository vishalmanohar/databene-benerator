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

package org.databene.benerator.engine;

import org.databene.benerator.Generator;
import org.databene.benerator.util.WrapperProvider;
import org.databene.benerator.wrapper.ProductWrapper;

/**
 * Uses a {@link Generator} to create the currently processed object.<br/><br/>
 * Created: 01.09.2011 19:03:38
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class CurrentProductGeneration implements Statement, LifeCycleHolder {
	
	private String instanceName;
	private Generator<Object> source;
	private WrapperProvider<Object> provider;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CurrentProductGeneration(String instanceName, Generator<?> source) {
		this.instanceName = instanceName;
		this.source = (Generator) source;
		this.provider = new WrapperProvider<Object>();
	}

	public void init(BeneratorContext context) {
		source.init(context);
	}

	public boolean execute(BeneratorContext context) {
		ProductWrapper<Object> wrapper = source.generate(provider.get());
		context.setCurrentProduct(wrapper);
		if (instanceName != null && wrapper != null) {
			// in normal descriptor execution, a BeneratorSubContext is used, 
			// in tests and generators exported from descriptor files, it may be a DefaultBeneratorContext
			BeneratorContext contextToUse = (context instanceof BeneratorSubContext ? 
					((BeneratorSubContext) context).getParent() : context);
			contextToUse.set(instanceName, wrapper.unwrap());
		}
		return (wrapper != null);
	}

	public void reset() {
		source.reset();
	}

	public void close() {
		source.close();
	}

	@Override
	public String toString() {
		return instanceName + ':' + source;
	}

}

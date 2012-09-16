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

package org.databene.benerator.wrapper;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.InvalidGeneratorSetupException;

/**
 * {@link Generator} implementation which wraps a source {@link Generator}
 * but generates a null value before forwarding the products of the source.<br/><br/>
 * Created: 04.07.2011 13:18:03
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class NullStartingGenerator<E> extends GeneratorProxy<E> {
	
	private boolean nullUsed;
	
	public NullStartingGenerator(Generator<E> source) {
		super(source);
		this.nullUsed = false;
	}
	
	@Override
	public void init(GeneratorContext context) throws InvalidGeneratorSetupException {
		super.init(context);
	}
	
	@Override
	public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
		if (!nullUsed) {
			this.nullUsed = true;
			return wrapper.wrap(null);
		}
		return super.generate(wrapper);
	}
	
	@Override
	public void reset() {
		nullUsed = false;
		super.reset();
	}
}

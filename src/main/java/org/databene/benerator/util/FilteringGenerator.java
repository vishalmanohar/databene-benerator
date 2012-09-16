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
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.script.Expression;

/**
 * Generator proxy which takes the input of another Generator and only 
 * passes it if a boolean expression evaluates to true.<br/><br/>
 * Created: 11.03.2010 14:23:53
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class FilteringGenerator<E> extends GeneratorProxy<E> {
	
	private Expression<Boolean> filter;

	public FilteringGenerator(Generator<E> source, Expression<Boolean> filter) {
	    super(source);
	    this.filter = filter;
    }

	@Override
	public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
		ProductWrapper<E> feed;
		while ((feed = super.generate(wrapper)) != null) {
			E candidate = feed.unwrap();
			context.set("_candidate", candidate);
			if (filter.evaluate(context))
				return wrapper.wrap(candidate);
		}
		return null;
	}
	
}

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

package org.databene.benerator.distribution;

import java.util.List;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.sample.SampleGenerator;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.benerator.wrapper.GeneratorProxy;

/**
 * Internal generator which reads all products of a source generator and provides them with an index-based strategy. 
 * Reevaluates source on reset.<br/><br/>
 * Created: 21.07.2010 01:57:31
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class IndexBasedSampleGeneratorProxy<E> extends GeneratorProxy<E> {
	
	private Generator<E> dataProvider;
	private Distribution distribution;
	private boolean unique;

	public IndexBasedSampleGeneratorProxy(Generator<E> dataProvider, Distribution distribution, boolean unique) {
		super(dataProvider.getGeneratedType());
		this.dataProvider = dataProvider;
		this.distribution = distribution;
		this.unique = unique;
    }
	
	@Override
	public void init(GeneratorContext context) {
		if (!dataProvider.wasInitialized())
			dataProvider.init(context);
		initMembers(context);
	    super.init(context);
	}
	
	@Override
	public void reset() {
	    dataProvider.reset();
	    initMembers(context);
	    super.reset();
	}
	
	@Override
	public void close() {
	    dataProvider.close();
	    super.close();
	}

	private void initMembers(GeneratorContext context) {
		List<E> products = GeneratorUtil.allProducts(dataProvider);
		SampleGenerator<E> sampleGen = new SampleGenerator<E>(
				dataProvider.getGeneratedType(), distribution, unique, products);
		sampleGen.init(context);
		setSource(sampleGen);
    }
	
}

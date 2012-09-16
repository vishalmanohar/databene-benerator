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

package org.databene.benerator.dataset;

import org.databene.benerator.Generator;
import org.databene.benerator.WeightedGenerator;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.benerator.wrapper.ProductWrapper;

/**
 * {@link DatasetBasedGenerator} implementation which bases on an atomic dataset.<br/><br/>
 * Created: 09.03.2011 10:54:28
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class AtomicDatasetGenerator<E> extends GeneratorProxy<E> implements WeightedDatasetGenerator<E> {

	private String nesting;
	private String dataset;
	private double weight;
	
	public AtomicDatasetGenerator(WeightedGenerator<E> source, String nesting, String dataset) {
		this(source, nesting, dataset, source.getWeight());
	}
	
	public AtomicDatasetGenerator(Generator<E> source, String nesting, String dataset, double weight) {
		super(source);
		this.nesting = nesting;
		this.dataset = dataset;
		this.weight = weight;
	}
	
	public String getNesting() {
		return nesting;
	}
	
	public String getDataset() {
		return dataset;
	}

	public double getWeight() {
		return weight;
	}
	
	@Override
	public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
		return super.generate(wrapper).setTag(nesting, dataset);
	}
	
	public E generateForDataset(String requestedDataset) {
		if (!dataset.equals(requestedDataset))
			throw new IllegalArgumentException("Requested dataset " + requestedDataset + ", but supporting only dataset " + this.dataset);
		ProductWrapper<E> wrapper = generate(getResultWrapper());
		if (wrapper == null)
			return null;
		return wrapper.unwrap();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + nesting + ":" + dataset + "]";
	}

}

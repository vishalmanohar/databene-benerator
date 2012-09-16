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

import java.util.HashSet;
import java.util.Set;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.WeightedGenerator;
import org.databene.benerator.util.RandomUtil;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.ConfigurationError;

/**
 * Abstract implementation of the {@link DatasetBasedGenerator} interface.
 * It is configured with 'nesting' and 'dataset'. Depending on the type of 
 * the dataset (atomic or composite), it initializes a delegate instance 
 * of a {@link DatasetBasedGenerator}, either a {@link CompositeDatasetGenerator}
 * or an {@link AtomicDatasetGenerator}. For the dfinition of custom 
 * {@link DatasetBasedGenerator}s, inherit from this class and implement 
 * the abstract method {@link #createAtomicDatasetGenerator(Dataset, boolean)}.
 * All dataset recognition and handling and data generation will be handled 
 * automatically.<br/><br/>
 * Created: 10.03.2011 10:44:58
 * @since 0.6.6
 * @author Volker Bergmann
 */
public abstract class AbstractDatasetGenerator<E> extends GeneratorProxy<E> implements DatasetBasedGenerator<E> {
    
    protected String nesting;
    protected String datasetName;
    protected Set<String> supportedDatasets;
    protected double totalWeight;
    protected boolean fallback;
    
    // constructor -----------------------------------------------------------------------------------------------------
    
    public AbstractDatasetGenerator(Class<E> generatedType, String nesting, String datasetName, boolean fallback) {
        super(generatedType);
        this.nesting = nesting;
        this.datasetName = datasetName;
        this.fallback = fallback;
        this.supportedDatasets = new HashSet<String>();
        this.supportedDatasets.add(datasetName);
        this.totalWeight = 0;
    }
    
	public boolean supportsDataset(String datasetName) {
		return supportedDatasets.contains(datasetName);
	}
	
    // DatasetBasedGenerator interface implementation ------------------------------------------------------------------
    
	public String getNesting() {
		return nesting;
	}
	
	public void setNesting(String nesting) {
		this.nesting = nesting;
	}
	
	public String getDataset() {
		return datasetName;
	}
	
	public void setDataset(String datasetName) {
		this.datasetName = datasetName;
		this.supportedDatasets.clear();
		this.supportedDatasets.add(datasetName);
	}
	
	public double getWeight() {
		return totalWeight;
	}
	
	@Override
	public synchronized void init(GeneratorContext context) {
		Dataset dataset = DatasetUtil.getDataset(nesting, datasetName);
		setSource(createDatasetGenerator(dataset, true, fallback));
		super.init(context);
	}
	
	public E generateForDataset(String requestedDataset) {
		DatasetBasedGenerator<E> sourceGen = getSource();
		if (sourceGen instanceof CompositeDatasetGenerator)
			return ((CompositeDatasetGenerator<E>) sourceGen).generateForDataset(requestedDataset);
		else { // assume that either the dataset matches or an appropriate failover has been chosen
			ProductWrapper<E> wrapper = sourceGen.generate(getResultWrapper());
			return (wrapper != null ? wrapper.unwrap() : null);
		}
	}
    
	public String randomDataset() {
		if (getSource() instanceof CompositeDatasetGenerator) {
			Dataset dataset = DatasetUtil.getDataset(nesting, datasetName);
			return RandomUtil.randomElement(dataset.getSubSets()).getName();
		} else
			return datasetName;
	}
	

	// helper methods --------------------------------------------------------------------------------------------------
	
    protected WeightedDatasetGenerator<E> createDatasetGenerator(Dataset dataset, boolean required, boolean fallback) {
    	WeightedDatasetGenerator<E> generator;
    	if (isAtomic(dataset))
			generator = createAtomicDatasetGenerator(dataset, required);
		else 
    		generator = createCompositeDatasetGenerator(dataset, required, fallback);
    	if (generator != null)
        	supportedDatasets.add(dataset.getName());
		return generator;
	}

	protected boolean isAtomic(Dataset dataset) {
		return dataset.isAtomic();
	}

    protected CompositeDatasetGenerator<E> createCompositeDatasetGenerator(Dataset dataset, boolean required, boolean fallback) {
		CompositeDatasetGenerator<E> generator = new CompositeDatasetGenerator<E>(nesting, dataset.getName(), fallback);
		for (Dataset subSet : dataset.getSubSets()) {
			WeightedDatasetGenerator<E> subGenerator = createDatasetGenerator(subSet, false, fallback);
			if (subGenerator != null)
				generator.addSubDataset(subGenerator, subGenerator.getWeight());
		}
		if (generator.getSource().getSources().size() > 0)
			return generator;
		if (required)
			throw new ConfigurationError("No samples defined for composite dataset: " + dataset.getName() + 
					" in generator " + this);
		else
			return null;
	}

	protected AtomicDatasetGenerator<E> createAtomicDatasetGenerator(Dataset dataset, boolean required) {
		WeightedGenerator<E> generator = createGeneratorForAtomicDataset(dataset);
		if (generator != null) {
	    	totalWeight += generator.getWeight();
			return new AtomicDatasetGenerator<E>(generator, nesting, dataset.getName());
		}
		if (required)
			throw new InvalidGeneratorSetupException("Unable to create generator for atomic dataset: " + dataset.getName());
		else
			return null;
	}

	protected abstract WeightedGenerator<E> createGeneratorForAtomicDataset(Dataset dataset);

	@Override
	public DatasetBasedGenerator<E> getSource() {
		return (DatasetBasedGenerator<E>) super.getSource();
	}
	
	
	// java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + nesting + ':' + datasetName + ']';
    }

}

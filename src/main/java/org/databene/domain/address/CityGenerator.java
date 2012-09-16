/*
 * (c) Copyright 2007-2011 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
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

package org.databene.domain.address;

import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.WeightedGenerator;
import org.databene.benerator.dataset.AbstractDatasetGenerator;
import org.databene.benerator.dataset.Dataset;
import org.databene.benerator.distribution.FeatureWeight;
import org.databene.benerator.distribution.IndividualWeight;
import org.databene.benerator.sample.IndividualWeightSampleGenerator;
import org.databene.benerator.util.GeneratorUtil;

/**
 * Generates {@link City} objects.<br/>
 * <br/>
 * Created: 14.10.2007 21:24:25
 * @author Volker Bergmann
 */
public class CityGenerator extends AbstractDatasetGenerator<City> implements NonNullGenerator<City> {

	private static final String REGION = "/org/databene/dataset/region";
	
    public CityGenerator(String dataset) {
    	super(City.class, REGION, dataset, true);
    }

    @Override
	protected boolean isAtomic(Dataset dataset) {
		Country country = Country.getInstance(dataset.getName(), false);
		return (country != null);
    }
    
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected WeightedGenerator<City> createGeneratorForAtomicDataset(Dataset dataset) {
		IndividualWeightSampleGenerator<City> generator = new IndividualWeightSampleGenerator<City>(
				City.class, (IndividualWeight) new FeatureWeight("population"));
		Country country = Country.getInstance(dataset.getName());
		country.checkCities();
        for (State state : country.getStates())
            for (City city : state.getCities())
                generator.addValue(city);
        return (generator.getVariety() > 0 ? generator : null);
	}

	public City generate() {
		return GeneratorUtil.generateNonNull(this);
	}

}

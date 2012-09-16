/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.csv;

import java.util.List;

import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.sample.SampleGeneratorUtil;
import org.databene.benerator.sample.SampleGenerator;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.commons.Context;
import org.databene.commons.Converter;
import org.databene.script.ScriptConverterForStrings;
import org.databene.script.WeightedSample;

/**
 * Generates values from a dataset based on a {@link Sequence}.<br/><br/>
 * Created: 17.02.2010 23:22:52
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class SequencedDatasetCSVGenerator<E> extends GeneratorProxy<E> {
	
    @SuppressWarnings("unchecked")
	public SequencedDatasetCSVGenerator(String filenamePattern, char separator, String datasetName, String nesting,
            Distribution distribution, String encoding, Context context) {
        this(filenamePattern, separator, datasetName, nesting, distribution, encoding, 
        		(Converter<String, E>) new ScriptConverterForStrings(context));
    }

	@SuppressWarnings("unchecked")
    public SequencedDatasetCSVGenerator(String filenamePattern, char separator, String datasetName, String nesting,
            Distribution distribution, String encoding, Converter<String, E> preprocessor) {
		super((Class<E>) Object.class);
        List<E> samples = parseFiles(datasetName, separator, nesting, filenamePattern, encoding, preprocessor);
		setSource(new SampleGenerator<E>((Class<E>) samples.get(0).getClass(), distribution, false, samples));
    }

	private List<E> parseFiles(String datasetName, char separator, String nesting, String filenamePattern,
            String encoding, Converter<String, E> preprocessor) {
        List<WeightedSample<E>> weightedSamples = CSVGeneratorUtil.parseDatasetFiles(datasetName, separator, nesting, filenamePattern, encoding, preprocessor);
        return SampleGeneratorUtil.extractValues(weightedSamples);
    }

}

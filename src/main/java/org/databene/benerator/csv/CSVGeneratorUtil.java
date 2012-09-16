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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.databene.benerator.dataset.DatasetUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Converter;
import org.databene.document.csv.CSVLineIterator;
import org.databene.script.WeightedSample;
import org.databene.webdecs.DataContainer;

/**
 * Provides CSV-related utility methods.<br/><br/>
 * Created: 17.02.2010 23:20:35
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class CSVGeneratorUtil {
	
    public static <T> List<WeightedSample<T>> parseDatasetFiles(
            String datasetName, char separator, String nesting, String filenamePattern,
            String encoding, Converter<String, T> converter) {
        String[] dataFilenames;
        if (nesting == null || datasetName == null)
        	dataFilenames = new String[] { filenamePattern };
        else
        	dataFilenames = DatasetUtil.getDataFiles(filenamePattern, datasetName, nesting);
        List<WeightedSample<T>> samples = new ArrayList<WeightedSample<T>>();
        for (String dataFilename : dataFilenames)
            parseFile(dataFilename, separator, encoding, converter, samples);
        return samples;
    }
    
    public static <T> List<WeightedSample<T>> parseFile(String filename, char separator, String encoding, 
    		Converter<String, T> converter) {
        return parseFile(filename, separator, encoding, converter, new ArrayList<WeightedSample<T>>());
    }

    public static <T> List<WeightedSample<T>> parseFile(String filename, char separator, String encoding, 
    		Converter<String, T> converter, List<WeightedSample<T>> samples) {
        try {
            CSVLineIterator iterator = new CSVLineIterator(filename, separator, encoding);
            DataContainer<String[]> container = new DataContainer<String[]>();
            while ((container = iterator.next(container)) != null) {
            	String[] tokens = container.getData();
                if (tokens.length == 0)
                    continue;
                double weight = (tokens.length < 2 ? 1. : Double.parseDouble(tokens[1]));
                T value = converter.convert(tokens[0]);
                WeightedSample<T> sample = new WeightedSample<T>(value, weight);
                samples.add(sample);
            }
            return samples;
        } catch (IOException e) {
            throw new ConfigurationError(e);
        }
    }

}

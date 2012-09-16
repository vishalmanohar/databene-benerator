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

package org.databene.benerator.factory;

import org.databene.benerator.Generator;
import org.databene.benerator.dataset.DatasetUtil;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.wrapper.DataSourceGenerator;
import org.databene.benerator.wrapper.IteratingGenerator;
import org.databene.commons.iterator.TextLineIterable;
import org.databene.document.csv.CSVCellSource;
import org.databene.document.csv.CSVSource;
import org.databene.document.xls.XLSLineSource;
import org.databene.model.data.Uniqueness;
import org.databene.webdecs.DataSource;

/**
 * Factory class for source-related {@link Generator}s.<br/><br/>
 * Created: 06.08.2011 13:11:11
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class SourceFactory {

    // source generators -----------------------------------------------------------------------------------------------

    /**
     * Creates a generator that iterates through the cells of a CSV file.
     *
     * @param uri         the uri of the CSV file
     * @param separator   the cell separator used in the CSV file
     * @return a generator of the desired characteristics
     */
    public static Generator<String> createCSVCellGenerator(String uri, char separator, String encoding) {
        return new DataSourceGenerator<String>(new CSVCellSource(uri, separator));
    }

    public static Generator<String[]> createCSVGenerator(String uri, char separator, String encoding, 
    		boolean ignoreEmptyLines, boolean rowBased) {
        return new DataSourceGenerator<String[]>(new CSVSource(uri, separator, encoding, ignoreEmptyLines, rowBased));
    }

    /**
     * Creates a generator that creates lines from a CSV file as String arrays.
     *
     * @param uri              the uri of the CSV file
     * @param separator        the cell separator used in the CSV file
     * @param encoding 
     * @param ignoreEmptyLines flag whether to leave out empty lines
     * @return a generator of the desired characteristics
     */
    public static Generator<String[]> createCSVLineGenerator(String uri, char separator, String encoding, 
    		boolean ignoreEmptyLines) {
        return new DataSourceGenerator<String[]>(new CSVSource(uri, separator, encoding, ignoreEmptyLines, true));
    }

    /**
     * Creates a generator that creates lines from a XLS file as {@link Object} arrays.
     * @param uri the uri of the XLS file
     * @return a generator of the desired characteristics
     */
    public static Generator<Object[]> createXLSLineGenerator(String uri) {
        return new DataSourceGenerator<Object[]>(new XLSLineSource(uri));
    }

    /**
     * Creates a generator that iterates through the lines of a text file.
     * @param uri         the URI of the text file
     * @return a generator of the desired characteristics
     */
    public static Generator<String> createTextLineGenerator(String uri) {
        return new IteratingGenerator<String>(new TextLineIterable(uri));
    }

    @SuppressWarnings("unchecked")
	public static <T> Generator<T> createRawSourceGenerator(String nesting, String dataset,
            String sourceName, DataSourceProvider<T> factory, Class<T> generatedType, BeneratorContext context) {
	    Generator<T> generator;
		if (dataset != null && nesting != null) {
		    String[] uris = DatasetUtil.getDataFiles(sourceName, dataset, nesting);
            Generator<T>[] sources = new Generator[uris.length];
            for (int i = 0; i < uris.length; i++) {
            	DataSource<T> source = factory.create(uris[i], context);
                sources[i] = new DataSourceGenerator<T>(source);
            }
			generator = context.getGeneratorFactory().createAlternativeGenerator(generatedType, sources, Uniqueness.NONE);
		} else {
		    // iterate over (possibly large) data file
			DataSource<T> source = factory.create(sourceName, context);
		    generator = new DataSourceGenerator<T>(source);
		}
		return generator;
    }

}

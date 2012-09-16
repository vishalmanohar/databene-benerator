/*
 * (c) Copyright 2008-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.csv;

import org.databene.platform.array.Array2EntityConverter;
import org.databene.webdecs.DataContainer;
import org.databene.webdecs.DataIterator;
import org.databene.webdecs.OrthogonalArrayIterator;
import org.databene.webdecs.util.ConvertingDataIterator;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.document.csv.CSVLineIterator;
import org.databene.commons.ArrayUtil;
import org.databene.commons.Converter;
import org.databene.commons.IOUtil;
import org.databene.commons.Patterns;
import org.databene.commons.StringUtil;
import org.databene.commons.Tabular;
import org.databene.commons.converter.ArrayConverter;
import org.databene.commons.converter.ConverterChain;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Iterates Entities in a CSV file.
 * When the property 'columns' is set, the CSV file is assumed to have no header row.<br/>
 * <br/>
 * Created: 07.04.2008 09:49:08
 * @since 0.5.1
 * @author Volker Bergmann
 */
public class CSVEntityIterator implements DataIterator<Entity>, Tabular {

    private String uri;
    private char separator;
    private String encoding;
    private String[] columns;
    private Converter<String, ?> preprocessor;
    private boolean expectingHeader;
    private boolean rowBased;

    private DataIterator<Entity> source;
    
    private boolean initialized;
    private ComplexTypeDescriptor entityDescriptor;

    // constructors ----------------------------------------------------------------------------------------------------
    
    public CSVEntityIterator(String uri, ComplexTypeDescriptor descriptor, Converter<String, ?> preprocessor, char separator, String encoding) throws FileNotFoundException {
    	if (!IOUtil.isURIAvailable(uri))
    		throw new FileNotFoundException("URI not found: " + uri);
        this.uri = uri;
        this.preprocessor = preprocessor;
        this.separator = separator;
        this.encoding = encoding;
        this.entityDescriptor = descriptor;
        this.initialized = false;
        this.expectingHeader = true;
        this.rowBased = (descriptor != null && descriptor.isRowBased() != null ? descriptor.isRowBased() : true);
    }
    
    // properties ------------------------------------------------------------------------------------------------------
    
	public void setExpectingHeader(boolean expectHeader) {
		this.expectingHeader = expectHeader;
	}
	
	public boolean isRowBased() {
		return rowBased;
	}
	
	public void setRowBased(boolean rowBased) {
		this.rowBased = rowBased;
	}
	
    public String[] getColumnNames() {
    	return columns;
    }
    
	public void setColumns(String[] columns) {
		this.expectingHeader = false;
		if (ArrayUtil.isEmpty(columns))
			this.columns = null;
		else {
	        this.columns = columns;
	        StringUtil.trimAll(this.columns);
		}
    }

    // DataIterator interface ------------------------------------------------------------------------------------------
    
	public Class<Entity> getType() {
		return Entity.class;
	}
	
	public DataContainer<Entity> next(DataContainer<Entity> container) {
    	assureInitialized();
        return source.next(container);
    }
    
	public void close() {
		 IOUtil.close(source);
	}

    public static List<Entity> parseAll(String uri, char separator, String encoding, ComplexTypeDescriptor descriptor, 
    		Converter<String, String> preprocessor, Patterns patterns) throws FileNotFoundException {
    	List<Entity> list = new ArrayList<Entity>();
    	CSVEntityIterator iterator = new CSVEntityIterator(uri, descriptor, preprocessor, separator, encoding);
    	DataContainer<Entity> container = new DataContainer<Entity>();
    	while ((container = iterator.next(container)) != null)
    		list.add(container.getData());
    	return list;
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
	public String toString() {
        return getClass().getSimpleName() + "[uri=" + uri + ", encoding=" + encoding + ", separator=" + separator +
                ", entityName=" + entityDescriptor.getName() + "]";
    }

    // private helpers -------------------------------------------------------------------------------------------------
    
    private void assureInitialized() {
    	if (!initialized) {
    		init();
    		initialized = true;
    	}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void init() {
		try {
			DataIterator<String[]> cellIterator;
				cellIterator = new CSVLineIterator(uri, separator, true, encoding);
				if (!rowBased)
					cellIterator = new OrthogonalArrayIterator<String>(cellIterator);
			if (expectingHeader)
				setColumns(cellIterator.next(new DataContainer<String[]>()).getData());
	        Converter<String[], Object[]> arrayConverter = new ArrayConverter(String.class, Object.class, preprocessor); 
	        Array2EntityConverter a2eConverter = new Array2EntityConverter(entityDescriptor, columns, true);
	        Converter<String[], Entity> converter = new ConverterChain<String[], Entity>(arrayConverter, a2eConverter);
	        this.source = new ConvertingDataIterator<String[], Entity>(cellIterator, converter);
		} catch (IOException e) {
			throw new RuntimeException("Error in processing " + uri, e);
		}
	}

}

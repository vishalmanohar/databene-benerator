/*
 * (c) Copyright 2007-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.csv;

import java.io.FileNotFoundException;

import org.databene.commons.ArrayUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Converter;
import org.databene.commons.StringUtil;
import org.databene.commons.SystemInfo;
import org.databene.commons.Tabular;
import org.databene.commons.converter.NoOpConverter;
import org.databene.document.csv.CSVUtil;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.FileBasedEntitySource;
import org.databene.webdecs.DataIterator;

/**
 * Imports {@link Entity} data from CSV files.<br/><br/>
 * @author Volker Bergmann
 */
public class CSVEntitySource extends FileBasedEntitySource implements Tabular {
	
    private char separator;
    private String encoding;
    private Converter<String, ?> preprocessor;

    private ComplexTypeDescriptor entityType;
	private String[] columnNames;
	private boolean expectingHeader;
	

    // constructors ----------------------------------------------------------------------------------------------------

    public CSVEntitySource() {
        this(null, null, SystemInfo.getFileEncoding());
    }
	
    public CSVEntitySource(String uri, ComplexTypeDescriptor entityType, String encoding) {
        this(uri, entityType, encoding, new NoOpConverter<String>(), ',');
    }
	
    public CSVEntitySource(String uri, ComplexTypeDescriptor entityType, String encoding, 
    		Converter<String, ?> preprocessor, char separator) {
        super(uri);
        this.separator = separator;
        this.encoding = encoding;
        this.entityType = entityType;
        this.preprocessor = preprocessor;
        this.expectingHeader = true;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

	public String[] getColumnNames() {
		if (ArrayUtil.isEmpty(columnNames)) {
			columnNames = StringUtil.trimAll(CSVUtil.parseHeader(uri, separator, encoding));
			expectingHeader = true;
		}
		return columnNames;
	}

	public void setColumns(String[] columns) {
		if (ArrayUtil.isEmpty(columns))
			this.columnNames = null;
		else {
	        this.columnNames = columns;
	        StringUtil.trimAll(this.columnNames);
			expectingHeader = false;
		}
    }
	
    // EntitySource interface ------------------------------------------------------------------------------------------

	public DataIterator<Entity> iterator() {
        try {
			CSVEntityIterator iterator = new CSVEntityIterator(resolveUri(), entityType, preprocessor, separator, encoding);
			if (!expectingHeader) {
				iterator.setColumns(getColumnNames());
				iterator.setExpectingHeader(false);
			}
			return iterator;
		} catch (FileNotFoundException e) {
			throw new ConfigurationError("Cannot create iterator. ", e);
		}
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
	public String toString() {
        return getClass().getSimpleName() + "[uri=" + uri + ", encoding=" + encoding + ", separator=" + separator +
                ", entityType=" + entityType.getName() + "]";
    }

}

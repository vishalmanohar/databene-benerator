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

package org.databene.platform.xls;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.factory.DataSourceProvider;
import org.databene.commons.Converter;
import org.databene.commons.converter.ArrayConverter;
import org.databene.document.xls.XLSSource;
import org.databene.webdecs.DataSource;
import org.databene.webdecs.util.ConvertingDataSource;
import org.databene.webdecs.util.OffsetDataSource;

/**
 * {@link DataSourceProvider} implementation which creates {@link XLSSource}s.<br/><br/>
 * Created: 19.07.2011 08:31:10
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class XLSArraySourceProvider implements DataSourceProvider<Object[]> {
	
	private Converter<?, ?> scriptConverter;
	private String emptyMarker;
	private String nullMarker;
	private boolean rowBased;
	
	public XLSArraySourceProvider(Converter<?, ?> scriptConverter, String emptyMarker, String nullMarker, boolean rowBased) {
	    this.scriptConverter = scriptConverter;
	    this.emptyMarker = emptyMarker;
	    this.nullMarker = nullMarker;
	    this.rowBased = rowBased;
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DataSource<Object[]> create(String uri, BeneratorContext context) {
		DataSource<Object[]> source = new XLSSource(uri, emptyMarker, nullMarker, rowBased);
		source = new OffsetDataSource<Object[]>(source, 1); // skip header row
        Converter<Object[], Object[]> converter = new ArrayConverter(Object.class, Object.class, scriptConverter); 
		return new ConvertingDataSource<Object[], Object[]>(source, converter);
	}

}

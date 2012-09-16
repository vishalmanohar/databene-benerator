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

package org.databene.platform.db;

import java.sql.Connection;
import java.sql.ResultSet;

import org.databene.commons.Context;
import org.databene.commons.Converter;
import org.databene.commons.StringUtil;
import org.databene.commons.converter.NoOpConverter;
import org.databene.jdbacl.QueryDataIterator;
import org.databene.script.ScriptConverterForStrings;
import org.databene.webdecs.DataIterator;
import org.databene.webdecs.DataSource;
import org.databene.webdecs.util.AbstractDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DataSource} implementation which is able to resolve script expressions, performs a query and 
 * provides the result of a query as a {@link DataIterator} of {@link ResultSet} objects.<br/><br/>
 * Created: 03.08.2011 19:55:52
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class QueryDataSource extends AbstractDataSource<ResultSet> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryIterable.class); 

    private final Connection connection;
    private final String query;
    private final int fetchSize;
    
    private Converter<String, ?> queryPreprocessor;
    private String renderedQuery;

    public QueryDataSource(Connection connection, String query, int fetchSize, Context context) {
    	super(ResultSet.class);
        if (connection == null)
            throw new IllegalStateException("'connection' is null");
        if (StringUtil.isEmpty(query))
            throw new IllegalStateException("'query' is empty or null");
        this.connection = connection;
        this.query = query;
        this.fetchSize = fetchSize;
        if (context != null)
        	this.queryPreprocessor = new ScriptConverterForStrings(context);
        else
        	this.queryPreprocessor = new NoOpConverter<String>();
       	LOGGER.debug("Constructed QueryIterable: {}", query);
    }

    public String getQuery() {
        return query;
    }

    public DataIterator<ResultSet> iterator() {
        renderedQuery = queryPreprocessor.convert(query).toString();
        return new QueryDataIterator(renderedQuery, connection, fetchSize);
    }
    
    @Override
    public String toString() {
    	return getClass().getSimpleName() + '[' + (renderedQuery != null ? renderedQuery : query) + ']';
    }

}

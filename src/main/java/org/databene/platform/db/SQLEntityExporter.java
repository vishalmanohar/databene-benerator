/*
 * (c) Copyright 2008-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.db;

import java.util.Set;
import java.util.Map.Entry;

import org.databene.benerator.consumer.TextFileExporter;
import org.databene.commons.ConfigurationError;
import org.databene.commons.version.VersionNumber;
import org.databene.jdbacl.DatabaseDialect;
import org.databene.jdbacl.DatabaseDialectManager;
import org.databene.model.data.Entity;
import org.databene.platform.csv.CSVEntityExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exports Entities to a SQL file.<br/><br/>
 * Created: 12.07.2008 09:43:59
 * @since 0.5.4
 * @author Volker Bergmann
 */
public class SQLEntityExporter extends TextFileExporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVEntityExporter.class);
    
    // defaults --------------------------------------------------------------------------------------------------------
    
    private static final String DEFAULT_URI = "export.sql";
    
    // attributes ------------------------------------------------------------------------------------------------------
    
    private DatabaseDialect dialect = null;
    private String dialectName;
    private VersionNumber dialectVersion;

    // constructors ----------------------------------------------------------------------------------------------------

    public SQLEntityExporter() {
        this(DEFAULT_URI);
    }
    
    public SQLEntityExporter(String uri) {
        this(uri, null);
    }

    public SQLEntityExporter(String uri, String dialect) {
    	this(uri, dialect, null, null);
    }

    public SQLEntityExporter(String uri, String dialect, String lineSeparator, String encoding) {
    	super(uri, encoding, lineSeparator);
    	setDialect(dialect);
    }
    
    public void setDialect(String dialectName) {
    	this.dialectName = dialectName;
    	if (dialectName != null)
    		this.dialect = DatabaseDialectManager.getDialectForProduct(dialectName, dialectVersion);
    }

    public void setVersion(String version) {
    	this.dialectVersion = VersionNumber.valueOf(version);
    	if (this.dialectName != null)
    		this.dialect = DatabaseDialectManager.getDialectForProduct(dialectName, dialectVersion);
    }

    // Callback methods for parent class functionality -----------------------------------------------------------------

	@Override
    protected void startConsumingImpl(Object object) {
        if (dialect == null)
        	throw new ConfigurationError("'dialect' not set in " + getClass().getSimpleName());
        LOGGER.debug("exporting {}", object);
        if (!(object instanceof Entity))
        	throw new IllegalArgumentException("Expected Entity");
        Entity entity = (Entity) object;
        String sql = createSQLInsert(entity);
        printer.println(sql);
    }

	@Override
    protected void postInitPrinter(Object object) {
    	// nothing special to do
    }

    String createSQLInsert(Entity entity) {
    	String table = entity.type();
        StringBuilder builder = new StringBuilder("insert into ");
        if (dialect.quoteTableNames)
        	builder.append('"').append(table).append('"');
        else
        	builder.append(table);
        builder.append(" (");
        Set<Entry<String, Object>> components = entity.getComponents().entrySet();
        boolean first = true;
        for (Entry<String, Object> entry : components) {
        	if (first)
        		first = false;
        	else
        		builder.append(", ");
        	builder.append(entry.getKey());
        }
        builder.append(") values (");
        first = true;
        for (Entry<String, Object> entry : components) {
			if (first)
				first = false;
			else
				builder.append(", ");
            Object value = entry.getValue();
			builder.append(dialect.formatValue(value));
        }
        builder.append(");");
        String sql = builder.toString();
        LOGGER.debug("built SQL statement: " + sql);
        return sql;
    }

}

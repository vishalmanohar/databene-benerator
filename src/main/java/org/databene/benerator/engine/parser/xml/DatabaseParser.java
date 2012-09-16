/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine.parser.xml;

import static org.databene.benerator.engine.DescriptorConstants.*;
import static org.databene.benerator.engine.parser.xml.DescriptorParserUtil.*;

import java.util.Set;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.BeneratorRootStatement;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.statement.DefineDatabaseStatement;
import org.databene.benerator.engine.statement.IfStatement;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.ConversionException;
import org.databene.script.Expression;
import org.databene.script.expression.DynamicExpression;
import org.databene.script.expression.FallbackExpression;
import org.w3c.dom.Element;

/**
 * Parses a &lt;database&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 00:40:56
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DatabaseParser extends AbstractBeneratorDescriptorParser {
	
	private static final Set<String> REQUIRED_ATTRIBUTES = CollectionUtil.toSet(ATT_ID);

	private static final Set<String> OPTIONAL_ATTRIBUTES = CollectionUtil.toSet(
			ATT_ENVIRONMENT, ATT_URL, ATT_DRIVER, ATT_USER, ATT_PASSWORD, ATT_CATALOG, ATT_SCHEMA, 
			ATT_TABLE_FILTER, ATT_INCL_TABLES, ATT_EXCL_TABLES, ATT_META_CACHE, ATT_BATCH, ATT_FETCH_SIZE, 
			ATT_READ_ONLY, ATT_LAZY, ATT_ACC_UNK_COL_TYPES);


	// TODO v1.0 define parser extension mechanism and move DatabaseParser and DefineDatabaseStatement to DB package?
	
	public DatabaseParser() {
	    super(EL_DATABASE, REQUIRED_ATTRIBUTES, OPTIONAL_ATTRIBUTES, 
	    		BeneratorRootStatement.class, IfStatement.class);
    }

	@Override
	@SuppressWarnings("unchecked")
    public DefineDatabaseStatement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
		// check preconditions
		assertAtLeastOneAttributeIsSet(element, ATT_ENVIRONMENT, ATT_DRIVER);
		assertAtLeastOneAttributeIsSet(element, ATT_ENVIRONMENT, ATT_URL);
		
		// parse
		try {
			Expression<String>  id            = parseAttribute(ATT_ID, element);
			Expression<String>  environment   = parseScriptableStringAttribute(ATT_ENVIRONMENT,  element);
			Expression<String>  url           = parseScriptableStringAttribute(ATT_URL,          element);
			Expression<String>  driver        = parseScriptableStringAttribute(ATT_DRIVER,       element);
			Expression<String>  user          = parseScriptableStringAttribute(ATT_USER,         element);
			Expression<String>  password      = parseScriptableStringAttribute(ATT_PASSWORD,     element);
			Expression<String>  catalog       = parseScriptableStringAttribute(ATT_CATALOG,      element);
			Expression<String>  schema        = parseScriptableStringAttribute(ATT_SCHEMA,       element);
			Expression<String>  tableFilter   = parseScriptableStringAttribute(ATT_TABLE_FILTER, element);
			Expression<String>  includeTables = parseScriptableStringAttribute(ATT_INCL_TABLES,  element);
			Expression<String>  excludeTables = parseScriptableStringAttribute(ATT_EXCL_TABLES,  element);
			Expression<Boolean> metaCache     = parseBooleanExpressionAttribute(ATT_META_CACHE,  element, false);
			Expression<Boolean> batch         = parseBooleanExpressionAttribute(ATT_BATCH,       element, false);
			Expression<Integer> fetchSize     = parseIntAttribute(ATT_FETCH_SIZE,                element, 100);
			Expression<Boolean> readOnly      = parseBooleanExpressionAttribute(ATT_READ_ONLY,   element, false);
			Expression<Boolean> lazy          = parseBooleanExpressionAttribute(ATT_LAZY,        element, true);
			Expression<Boolean> acceptUnknownColumnTypes = new FallbackExpression<Boolean>(
					parseBooleanExpressionAttribute(ATT_ACC_UNK_COL_TYPES, element), 
					new GlobalAcceptUnknownSimpleTypeExpression());
			return new DefineDatabaseStatement(id, environment, url, driver, user, password, catalog, schema, 
					metaCache, tableFilter, includeTables, excludeTables,
					batch, fetchSize, readOnly, lazy, acceptUnknownColumnTypes, context.getResourceManager());
		} catch (ConversionException e) {
			throw new ConfigurationError(e);
		}
    }

	static class GlobalAcceptUnknownSimpleTypeExpression extends DynamicExpression<Boolean> {
		public Boolean evaluate(Context context) {
            return ((BeneratorContext) context).isAcceptUnknownSimpleTypes();
        }
	}

}

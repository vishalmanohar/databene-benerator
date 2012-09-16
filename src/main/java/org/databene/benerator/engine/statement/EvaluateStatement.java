/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine.statement;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

import org.databene.benerator.StorageSystem;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.Statement;
import org.databene.commons.Assert;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.ConversionException;
import org.databene.commons.ErrorHandler;
import org.databene.commons.IOUtil;
import org.databene.commons.LogCategories;
import org.databene.commons.ReaderLineIterator;
import org.databene.commons.ShellUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.Level;
import org.databene.commons.SystemInfo;
import org.databene.commons.converter.LiteralParser;
import org.databene.jdbacl.DBExecutionResult;
import org.databene.jdbacl.DBUtil;
import org.databene.platform.db.DBSystem;
import org.databene.script.Expression;
import org.databene.script.Script;
import org.databene.script.ScriptUtil;
import org.databene.script.expression.ExpressionUtil;
import org.databene.task.TaskException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes an &lt;evaluate/&gt; from an XML descriptor.<br/>
 * <br/>
 * Created at 23.07.2009 17:59:36
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class EvaluateStatement implements Statement {
	
	private static final Logger logger = LoggerFactory.getLogger(EvaluateStatement.class);
	
	private static final String SHELL = "shell";
	
	private static final Map<String, String> extensionMap;
	
	static {
		try {
			extensionMap = IOUtil.readProperties("org/databene/benerator/engine/statement/fileTypes.properties");
		} catch (IOException e) {
			throw new ConfigurationError(e);
		}
	}
	
	boolean evaluate;
	Expression<String> idEx;
	Expression<String> textEx;
	Expression<String> uriEx;
	Expression<String> typeEx;
	Expression<?> targetObjectEx;
	Expression<Character> separatorEx;
	Expression<String> onErrorEx;
	Expression<String> encodingEx;
    Expression<Boolean> optimizeEx;
    Expression<Boolean> invalidateEx;
    Expression<?> assertionEx;

    public EvaluateStatement(boolean evaluate, Expression<String> idEx, Expression<String> textEx, 
    		Expression<String> uriEx, Expression<String> typeEx, Expression<?> targetObjectEx,
    		Expression<Character> separatorEx, Expression<String> onErrorEx, Expression<String> encodingEx, 
    		Expression<Boolean> optimizeEx, Expression<Boolean> invalidateEx, Expression<?> assertionEx) {
    	this.evaluate = evaluate;
    	this.idEx = idEx;
    	this.textEx = textEx;
    	this.uriEx = uriEx;
    	this.typeEx = typeEx;
    	this.targetObjectEx = targetObjectEx;
    	this.separatorEx = separatorEx;
    	this.onErrorEx = onErrorEx;
    	this.encodingEx = encodingEx;
    	this.optimizeEx = optimizeEx;
    	this.invalidateEx = invalidateEx;
    	this.assertionEx = assertionEx;
    }
    
    public Expression<String> getTextEx() {
    	return textEx;
    }

	public boolean execute(BeneratorContext context) {
		try {
			String onErrorValue = ExpressionUtil.evaluate(onErrorEx, context);
			if (onErrorValue == null)
				onErrorValue = "fatal";
			
			String typeValue = ExpressionUtil.evaluate(typeEx, context);
			// if type is not defined, derive it from the file extension
			String uriValue = ExpressionUtil.evaluate(uriEx, context);
			if (typeEx == null && uriEx != null) {
				typeValue = mapExtensionOf(uriValue);
				if ("winshell".equals(typeValue))
					if (!SystemInfo.isWindows())
						throw new ConfigurationError("Need Windows to run file: " + uriValue);
					else
						typeValue = SHELL;
				else if ("unixshell".equals(typeValue))
					if (SystemInfo.isWindows())
						throw new ConfigurationError("Need Unix system to run file: " + uriValue);
					else
						typeValue = SHELL;
			}
			if (uriValue != null)
				uriValue = context.resolveRelativeUri(uriValue);
			Object targetObject = ExpressionUtil.evaluate(targetObjectEx, context);
			if (typeValue == null && targetObject instanceof DBSystem)
				typeValue = "sql";
			if (typeValue == null && targetObject instanceof StorageSystem)
				typeValue = "execute";
            String encoding = ExpressionUtil.evaluate(encodingEx, context);

			// run
			Object result = null;
			String text = ExpressionUtil.evaluate(textEx, context);
			if ("sql".equals(typeValue)) {
	            Character separator = ExpressionUtil.evaluate(separatorEx, context);
	            if (separator == null)
	            	separator = ';';
	            result = runSql(uriValue, targetObject, onErrorValue, encoding, 
	            		text, separator, 
	            		optimizeEx.evaluate(context), invalidateEx.evaluate(context)).result;
            } else if (SHELL.equals(typeValue)) {
				result = runShell(uriValue, text, onErrorValue);
            } else if ("execute".equals(typeValue)) {
		        result = ((StorageSystem) targetObject).execute(text);
            } else {
            	if (typeValue == null) 
            		typeValue = context.getDefaultScript();
				if (!StringUtil.isEmpty(uriValue))
					text = IOUtil.getContentOfURI(uriValue);
				result = runScript(text, typeValue, onErrorValue, context);
			}
			context.set("result", result);
			Object assertionValue = ExpressionUtil.evaluate(assertionEx, context);
			if (assertionValue instanceof String)
				assertionValue = LiteralParser.parse((String) assertionValue);
			if (assertionValue != null && !(assertionValue instanceof String && ((String) assertionValue).length() == 0)) {
				if (assertionValue instanceof Boolean) {
					if (!(Boolean) assertionValue)
						getErrorHandler(onErrorValue).handleError("Assertion failed: '" + assertionEx + "'");
				} else {
					if (!BeanUtil.equalsIgnoreType(assertionValue, result))
						getErrorHandler(onErrorValue).handleError("Assertion failed. Expected: '" + assertionValue + "', found: '" + result + "'");
				}
			}
			String idValue = ExpressionUtil.evaluate(idEx, context);
			if (idValue != null)
				context.set(idValue, result);
	    	return true;
		} catch (ConversionException e) {
			throw new ConfigurationError(e);
		} catch (IOException e) {
			throw new ConfigurationError(e);
		}
    }

	private String mapExtensionOf(String uri) {
		String lcUri = uri.toLowerCase();
		for (Entry<String, String> entry : extensionMap.entrySet())
			if (lcUri.endsWith(entry.getKey()))
				return entry.getValue();
	    return null;
    }

	private ErrorHandler getErrorHandler(String level) {
	    ErrorHandler errorHandler = new ErrorHandler(getClass().getName(), Level.valueOf(level));
	    return errorHandler;
    }

	private Object runScript(String text, String type, String onError, Context context) {
		ErrorHandler errorHandler = new ErrorHandler(getClass().getName(),
				Level.valueOf(onError));
		try {
			Script script = ScriptUtil.parseScriptText(text, type);
			return script.evaluate(context);
		} catch (Exception e) {
			errorHandler.handleError("Error in script evaluation", e);
			return null;
		}
	}

	private Object runShell(String uri, String text, String onError) {
		ErrorHandler errorHandler = new ErrorHandler(getClass().getName(), Level.valueOf(onError));
		StringWriter writer = new StringWriter();
		if (text != null)
			ShellUtil.runShellCommands(new ReaderLineIterator(new StringReader(text)), writer, errorHandler);
		else if (uri != null)
			ShellUtil.runShellCommand(uri, writer, errorHandler);
		else
			throw new ConfigurationError("At least uri or text must be provided in <execute> and <evaluate>");
		String output = writer.toString();
		System.out.println(output);
		return LiteralParser.parse(output);
	}

	private DBExecutionResult runSql(String uri, Object targetObject, String onError,
			String encoding, String text, char separator, boolean optimize, Boolean invalidate) {
		if (targetObject == null)
			throw new ConfigurationError("Please specify the 'target' database to execute the SQL script");
		Assert.instanceOf(targetObject, DBSystem.class, "target");
		DBSystem db = (DBSystem) targetObject;
		if (uri != null)
			logger.info("Executing script " + uri);
		else if (text != null)
			logger.info("Executing inline script");
		else
			throw new TaskException("No uri or content");
        Connection connection = null;
        DBExecutionResult result = null;
		ErrorHandler errorHandler = new ErrorHandler(LogCategories.SQL, Level.valueOf(onError));
        try {
            connection = db.getConnection();
            if (text != null)
            	result = DBUtil.executeScript(text, separator, connection, optimize, errorHandler);
            else
            	result = DBUtil.executeScriptFile(uri, encoding, separator, connection, optimize, errorHandler);
            if (Boolean.TRUE.equals(invalidate) || (invalidate == null && !evaluate && result.changedStructure))
            	db.invalidate(); // possibly we changed the database structure
		} catch (Exception sqle) { 
            if (connection != null) {
            	try {
                    connection.rollback();
                } catch (SQLException e) {
                    // ignore this 2nd exception, we have other problems now (sqle)
                }
            }
            errorHandler.handleError("Error in SQL script execution", sqle);
        }
		return result;
	}

}

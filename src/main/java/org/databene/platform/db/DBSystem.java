/*
 * (c) Copyright 2007-2012 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.Consumer;
import org.databene.benerator.StorageSystem;
import org.databene.benerator.storage.AbstractStorageSystem;
import org.databene.benerator.storage.StorageSystemInserter;
import org.databene.commons.*;
import org.databene.commons.bean.ArrayPropertyExtractor;
import org.databene.commons.collection.OrderedNameMap;
import org.databene.commons.converter.AnyConverter;
import org.databene.commons.version.VersionNumber;
import org.databene.jdbacl.*;
import org.databene.jdbacl.dialect.OracleDialect;
import org.databene.jdbacl.model.*;
import org.databene.jdbacl.model.cache.CachingDBImporter;
import org.databene.jdbacl.model.jdbc.JDBCDBImporter;
import org.databene.jdbacl.model.jdbc.JDBCMetaDataUtil;
import org.databene.model.data.*;
import org.databene.script.PrimitiveType;
import org.databene.script.expression.ConstantExpression;
import org.databene.webdecs.DataSource;
import org.databene.webdecs.util.ConvertingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RDBMS implementation of the {@link StorageSystem} interface.<br/>
 * <br/>
 * Created: 27.06.2007 23:04:19
 * @since 0.3
 * @author Volker Bergmann
 */
public class DBSystem extends AbstractStorageSystem {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DBSystem.class);
	static final Logger JDBC_LOGGER = LoggerFactory.getLogger(LogCategories.JDBC);

    
    private static final int DEFAULT_FETCH_SIZE = 100;

	private static final VersionNumber MIN_ORACLE_VERSION = VersionNumber.valueOf("10.2.0.4");
	
	// constants -------------------------------------------------------------------------------------------------------
    
    protected static final ArrayPropertyExtractor<String> nameExtractor
            = new ArrayPropertyExtractor<String>("name", String.class);
    
	private static final TypeDescriptor[] EMPTY_TYPE_DESCRIPTOR_ARRAY = new TypeDescriptor[0];
    
    // attributes ------------------------------------------------------------------------------------------------------
    
    private String id;
    private String environment;
    private String url;
    private String user;
    private String password;
    private String driver;
    private String catalogName;
    private String schemaName;
    private String includeTables;
    private String excludeTables;
    boolean metaDataCache;
    boolean batch;
    boolean readOnly;
    boolean lazy;
    boolean acceptUnknownColumnTypes;
    
    private int fetchSize;

    private Database database;

    private Map<Thread, ThreadContext> threadContexts;
    private Map<String, TypeDescriptor> typeDescriptors;
    Map<String, DBTable> tables;
    
    private TypeMapper driverTypeMapper;
    DatabaseDialect dialect;
    private boolean dynamicQuerySupported;
    
	private boolean connectedBefore;
	private AtomicInteger invalidationCount;
	private DBMetaDataImporter importer;
	
    // constructors ----------------------------------------------------------------------------------------------------

    public DBSystem(String id, String url, String driver, String user, String password, DataModel dataModel) {
    	this(id, dataModel);
        setUrl(url);
        setUser(user);
        setPassword(password);
        setDriver(driver);
        checkOracleDriverVersion(driver);
    }

    public DBSystem(String id, String environment, DataModel dataModel) {
    	this(id, dataModel);
        setEnvironment(environment);
    }

	private DBSystem(String id, DataModel dataModel) {
        setId(id);
        setDataModel(dataModel);
        setSchema(null);
        setIncludeTables(".*");
        setExcludeTables(null);
        setFetchSize(DEFAULT_FETCH_SIZE);
        setMetaDataCache(false);
        setBatch(false);
        setReadOnly(false);
        setLazy(true);
        setDynamicQuerySupported(true);
        this.typeDescriptors = null;
        this.threadContexts = new HashMap<Thread, ThreadContext>();
        this.driverTypeMapper = driverTypeMapper();
        this.connectedBefore = false;
        this.invalidationCount = new AtomicInteger();
    }

	// properties ------------------------------------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

	public String getEnvironment() {
		return (environment != null ? environment : user);
	}

    private void setEnvironment(String environment) {
    	if (StringUtil.isEmpty(environment)) {
    		this.environment = null;
    		return;
    	}
    	LOGGER.debug("setting environment '{}'", environment);
		JDBCConnectData connectData = DBUtil.getConnectData(environment);
		this.environment = environment;
		this.url = connectData.url;
		this.driver = connectData.driver;
		this.catalogName = connectData.catalog;
		this.schemaName = connectData.schema;
		this.user = connectData.user;
		this.password = connectData.password;
	}

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = StringUtil.emptyToNull(password);
    }

    public String getCatalog() {
    	return catalogName;
    }

	public void setCatalog(String catalog) {
    	this.catalogName = catalog;
    }

	public String getSchema() {
        return schemaName;
    }

    public void setSchema(String schema) {
        this.schemaName = StringUtil.emptyToNull(StringUtil.trim(schema));
    }
    
    @Deprecated
	public void setTableFilter(String tableFilter) {
    	setIncludeTables(tableFilter);
    }

	public String getIncludeTables() {
    	return includeTables;
    }

	public void setIncludeTables(String includeTables) {
    	this.includeTables = includeTables;
    }

	public String getExcludeTables() {
    	return excludeTables;
    }

	public void setExcludeTables(String excludeTables) {
    	this.excludeTables = excludeTables;
    }

	public boolean isMetaDataCache() {
		return metaDataCache;
	}
	
	public void setMetaDataCache(boolean metaDataCache) {
		this.metaDataCache = metaDataCache;
	}
	
    public boolean isBatch() {
        return batch;
    }

    public void setBatch(boolean batch) {
        this.batch = batch;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }
    
    public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

    public boolean isLazy() {
		return lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public void setDynamicQuerySupported(boolean dynamicQuerySupported) {
    	this.dynamicQuerySupported = dynamicQuerySupported;
    }

	public void setAcceptUnknownColumnTypes(boolean acceptUnknownColumnTypes) {
    	this.acceptUnknownColumnTypes = acceptUnknownColumnTypes;
    }

    

    // DescriptorProvider interface ------------------------------------------------------------------------------------

	public TypeDescriptor[] getTypeDescriptors() {
        LOGGER.debug("getTypeDescriptors()");
        parseMetadataIfNecessary();
        if (typeDescriptors == null)
        	return EMPTY_TYPE_DESCRIPTOR_ARRAY;
        else
        	return CollectionUtil.toArray(typeDescriptors.values(), TypeDescriptor.class);
    }

    public TypeDescriptor getTypeDescriptor(String tableName) {
        LOGGER.debug("getTypeDescriptor({})", tableName);
        parseMetadataIfNecessary();
        TypeDescriptor entityDescriptor = typeDescriptors.get(tableName);
        if (entityDescriptor == null)
            for (TypeDescriptor candidate : typeDescriptors.values())
                if (candidate.getName().equalsIgnoreCase(tableName)) {
                    entityDescriptor = candidate;
                    break;
                }
        return entityDescriptor;
    }

    // StorageSystem interface -----------------------------------------------------------------------------------------

    public void store(Entity entity) {
		if (readOnly)
			throw new IllegalStateException("Tried to insert rows into table '" + entity.type() + "' " +
					"though database '" + id + "' is read-only");
        LOGGER.debug("Storing {}", entity);
        persistOrUpdate(entity, true);
    }

	public void update(Entity entity) {
		if (readOnly)
			throw new IllegalStateException("Tried to update table '" + entity.type() + "' " +
					"though database '" + id + "' is read-only");
        LOGGER.debug("Updating {}", entity);
        persistOrUpdate(entity, false);
	}

	public void flush() {
        LOGGER.debug("flush()");
    	for (ThreadContext threadContext : threadContexts.values())
    		threadContext.commit();
    }

    public void close() {
        LOGGER.debug("close()");
        flush();
        Iterator<ThreadContext> iterator = threadContexts.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().close();
            iterator.remove();
        }
        if (database != null)
        	CachingDBImporter.updateCacheFile(database);
        IOUtil.close(importer);
    }

	public Entity queryEntityById(String tableName, Object id) {
        try {
	        LOGGER.debug("queryEntityById({}, {})", tableName, id);
	        ComplexTypeDescriptor descriptor = (ComplexTypeDescriptor) getTypeDescriptor(tableName);
	        PreparedStatement query = getThreadContext().getSelectByPKStatement(descriptor);
	        query.setObject(1, id); // TODO v0.7.6 support composite keys
	        ResultSet resultSet = query.executeQuery();
	        if (resultSet.next())
	        	return ResultSet2EntityConverter.convert(resultSet, descriptor);
	        else
	        	return null;
        } catch (SQLException e) {
	        throw new RuntimeException("Error querying " + tableName, e);
        }
    }

    @SuppressWarnings("null")
    public DataSource<Entity> queryEntities(String type, String selector, Context context) {
        LOGGER.debug("queryEntities({})", type);
    	Connection connection = getThreadContext().connection;
        boolean script = false;
    	if (selector != null && selector.startsWith("{") && selector.endsWith("}")) {
    		selector = selector.substring(1, selector.length() - 1);
    		script = true;
    	}
    	String sql = null;
    	if (StringUtil.isEmpty(selector))
    	    sql = "select * from " + type;
    	else if (StringUtil.startsWithIgnoreCase(selector, "select") || StringUtil.startsWithIgnoreCase(selector, "'select"))
    	    sql = selector;
    	else if (selector.startsWith("ftl:") || !script)
    	    sql = "select * from " + type + " WHERE " + selector;
    	else
    	    sql = "'select * from " + type + " WHERE ' + " + selector;
    	if (script)
    		sql = '{' + sql + '}';
        DataSource<ResultSet> source = createQuery(sql, context, connection);
        return new EntityResultSetDataSource(source, (ComplexTypeDescriptor) getTypeDescriptor(type));
    }

    public long countEntities(String tableName) {
        LOGGER.debug("countEntities({})", tableName);
        String query = "select count(*) from " + tableName;
        return DBUtil.queryLong(query, getThreadContext().connection);
    }

    public DataSource<?> queryEntityIds(String tableName, String selector, Context context) {
        LOGGER.debug("queryEntityIds({}, {})", tableName, selector);
        
        // check for script
        boolean script = false;
    	if (selector != null && selector.startsWith("{") && selector.endsWith("}")) {
    		selector = selector.substring(1, selector.length() - 1);
    		script = true;
    	}

    	// find out pk columns
    	DBTable table = getTable(tableName);
        String[] pkColumnNames = table.getPKColumnNames();
        if (pkColumnNames.length == 0)
        	throw new ConfigurationError("Cannot create reference to table " + tableName + " since it does not define a primary key");
        
        // construct selector
        String query = "select " + ArrayFormat.format(pkColumnNames) + " from " + tableName;
        if (selector != null) {
        	if (script)
        		query = "{'" + query + " where ' + " + selector + "}";
        	else
        		query += " where " + selector;
        }
        return query(query, true, context);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public DataSource<?> query(String query, boolean simplify, Context context) {
        LOGGER.debug("query({})", query);
        Connection connection = getThreadContext().connection;
        QueryDataSource resultSetIterable = createQuery(query, context, connection);
        ResultSetConverter converter = new ResultSetConverter(Object.class, simplify);
		return new ConvertingDataSource<ResultSet, Object>(resultSetIterable, converter);
    }
    
    public Consumer inserter() {
    	return new StorageSystemInserter(this);
    }
    
    public Consumer inserter(String tableName) {
    	return new StorageSystemInserter(this, (ComplexTypeDescriptor) getTypeDescriptor(tableName));
    }
    
    // database-specific interface -------------------------------------------------------------------------------------

    public boolean tableExists(String tableName) {
        LOGGER.debug("tableExists({})", tableName);
        return (getTypeDescriptor(tableName) != null);
    }

    public void createSequence(String name) throws SQLException {
		getDialect().createSequence(name, 1, getThreadContext().connection);
    }

    public void dropSequence(String name) {
        execute(getDialect().renderDropSequence(name));
    }

    @Override
	public Object execute(String sql) {
    	try {
	        DBUtil.executeUpdate(sql, getConnection());
	        return null;
        } catch (SQLException e) {
	        throw new RuntimeException(e);
        }
    }
    
    public long nextSequenceValue(String sequenceName) {
    	return DBUtil.queryLong(getDialect().renderFetchSequenceValue(sequenceName), getThreadContext().connection);
    }
    
    public void setSequenceValue(String sequenceName, long value) throws SQLException {
    	getDialect().setNextSequenceValue(sequenceName, value, getThreadContext().connection);
    }
    
    Connection createConnection() {
		try {
            Connection connection = DBUtil.connect(url, driver, user, password, readOnly);
            if (!connectedBefore) {
            	DBUtil.logMetaData(connection);
            	connectedBefore = true;
            }
            connection.setAutoCommit(false);
            return connection;
        } catch (ConnectFailedException e) {
            throw new RuntimeException("Connecting the database failed. URL: " + url, e);
		} catch (SQLException e) {
			throw new ConfigurationError("Turning off auto-commit failed", e);
		}
	}

    public Connection getConnection() {
        return getThreadContext().connection;
    }
    
	public void invalidate() {
		typeDescriptors = null;
		tables = null;
		invalidationCount.incrementAndGet();
		if (environment != null) {
			File bufferFile = CachingDBImporter.getCacheFile(environment);
			if (bufferFile.exists()) {
				if (!bufferFile.delete() && metaDataCache) {
					LOGGER.error("Deleting " + bufferFile + " failed");
					metaDataCache = false;
				} else
					LOGGER.info("Deleted meta data cache file: " + bufferFile);

			}
		}
	} 
	
	public int invalidationCount() {
		return invalidationCount.get();
	}
	
	public void parseMetaData() {
        this.tables = new HashMap<String, DBTable>();
        this.typeDescriptors = OrderedNameMap.<TypeDescriptor>createCaseIgnorantMap();
        //this.tableColumnIndexes = new HashMap<String, Map<String, Integer>>();
        getDialect(); // make sure dialect is initialized
        database = getDbMetaData();
        if (lazy)
        	LOGGER.info("Fetching table details and ordering tables by dependency");
        else
        	LOGGER.info("Ordering tables by dependency");
        List<DBTable> tables = DBUtil.dependencyOrderedTables(database);
        for (DBTable table : tables)
            parseTable(table);
    }
	
    public DatabaseDialect getDialect() {
    	if (dialect == null) {
        	try {
        		DatabaseMetaData metaData = getThreadContext().connection.getMetaData();
				String productName = metaData.getDatabaseProductName();
                VersionNumber productVersion = VersionNumber.valueOf(metaData.getDatabaseMajorVersion() + "." + 
                		metaData.getDatabaseMinorVersion());
				dialect = DatabaseDialectManager.getDialectForProduct(productName, productVersion);
    		} catch (SQLException e) {
    	        throw new ConfigurationError("Database meta data access failed", e);
    		}
    	}
    	return dialect;
    }
    
    public String getSystem() {
    	return getDialect().getSystem();
    }
    
	public Database getDbMetaData() {
		if (database == null)
			fetchDbMetaData();
        return database;
	}

	// java.lang.Object overrides ------------------------------------------------------------------
	
    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + user + '@' + url + ']';
    }

    // private helpers ------------------------------------------------------------------------------

	private void checkOracleDriverVersion(String driver) {
		if (driver != null && driver.contains("oracle")) {
        	Connection connection = null;
    		try {
				connection = getConnection();
				DatabaseMetaData metaData = connection.getMetaData();
				VersionNumber driverVersion = VersionNumber.valueOf(metaData.getDriverVersion());
				if (driverVersion.compareTo(MIN_ORACLE_VERSION) < 0)
					LOGGER.warn("Your Oracle driver has a bug in metadata support. Please update to 10.2.0.4 or newer. " +
							"You can use that driver for accessing an Oracle 9 server as well.");
			} catch (SQLException e) {
				throw new ConfigurationError(e);
			} finally {
				close();
			}
        }
	}

	private void fetchDbMetaData() {
		try {
		    importer = createJDBCImporter();
		    if (metaDataCache)
		    	importer = new CachingDBImporter((JDBCDBImporter) importer, getEnvironment());
		    database = importer.importDatabase();
		} catch (ConnectFailedException e) {
			throw new ConfigurationError("Database not available. ", e);
		} catch (ImportFailedException e) {
		    throw new ConfigurationError("Unexpected failure of database meta data import. ", e);
		}
	}

	private JDBCDBImporter createJDBCImporter() {
		JDBCDBImporter importer = JDBCMetaDataUtil.getJDBCDBImporter(getConnection(), user, schemaName, 
				true, false, false, false, includeTables, excludeTables);
		return importer;
	}

	private QueryDataSource createQuery(String query, Context context, Connection connection) {
	    return new QueryDataSource(connection, query, fetchSize, (dynamicQuerySupported ? context : null));
    }
      
    private PreparedStatement getStatement(
    		ComplexTypeDescriptor descriptor, boolean insert, List<ColumnInfo> columnInfos) {
        ThreadContext context = getThreadContext();
        return context.getStatement(descriptor, insert, columnInfos);
    }

    private void parseTable(DBTable table) {
        LOGGER.debug("Parsing table {}" + table);
        String tableName = table.getName();
        tables.put(tableName.toUpperCase(), table);
        ComplexTypeDescriptor complexType;
        if (lazy) 
        	complexType = new LazyTableComplexTypeDescriptor(table, this);
        else
        	complexType = mapTableToComplexTypeDescriptor(table, new ComplexTypeDescriptor(tableName, this));
        typeDescriptors.put(tableName, complexType);
    }

	public ComplexTypeDescriptor mapTableToComplexTypeDescriptor(DBTable table, ComplexTypeDescriptor complexType) {
        // process primary keys
        DBPrimaryKeyConstraint pkConstraint = table.getPrimaryKeyConstraint();
        if (pkConstraint != null) {
	        String[] pkColumnNames = pkConstraint.getColumnNames();
	        if (pkColumnNames.length == 1) { // TODO v0.7.6 support composite primary keys
	        	String columnName = pkColumnNames[0];
	        	DBColumn column = table.getColumn(columnName);
				table.getColumn(columnName);
	            String abstractType = JdbcMetaTypeMapper.abstractType(column.getType(), acceptUnknownColumnTypes);
	        	IdDescriptor idDescriptor = new IdDescriptor(columnName, this, abstractType);
				complexType.setComponent(idDescriptor);
	        }
        }

        // process foreign keys
        for (DBForeignKeyConstraint constraint : table.getForeignKeyConstraints()) {
            String[] foreignKeyColumnNames = constraint.getForeignKeyColumnNames();
            if (foreignKeyColumnNames.length == 1) {
                String fkColumnName = foreignKeyColumnNames[0];
                DBTable targetTable = constraint.getRefereeTable();
                DBColumn fkColumn = constraint.getTable().getColumn(fkColumnName);
                DBDataType concreteType = fkColumn.getType();
                String abstractType = JdbcMetaTypeMapper.abstractType(concreteType, acceptUnknownColumnTypes);
                ReferenceDescriptor descriptor = new ReferenceDescriptor(
                        fkColumnName, 
                        this,
                        abstractType,
                        targetTable.getName(),
                        constraint.getRefereeColumnNames()[0]);
                descriptor.getLocalType(false).setSource(id);
                descriptor.setMinCount(new ConstantExpression<Long>(1L));
                descriptor.setMaxCount(new ConstantExpression<Long>(1L));
                boolean nullable = fkColumn.isNullable();
				descriptor.setNullable(nullable);
                complexType.setComponent(descriptor); // overwrite possible id descriptor for foreign keys
                LOGGER.debug("Parsed reference " + table.getName() + '.' + descriptor);
            } else {
                // TODO v0.7.6 handle composite keys
            }
        }
        // process normal columns
        for (DBColumn column : table.getColumns()) {
            LOGGER.debug("parsing column: {}", column);
            String columnName = column.getName();
            if (complexType.getComponent(columnName) != null)
                continue; // skip columns that were already parsed (fk)
            String columnId = table.getName() + '.' + columnName;
            if (column.isVersionColumn()) {
                LOGGER.debug("Leaving out version column {}", columnId);
                continue;
            }
            DBDataType columnType = column.getType();
            String type = JdbcMetaTypeMapper.abstractType(columnType, acceptUnknownColumnTypes);
            if(type == null){
                LOGGER.debug(String.format("Could not find type. Leaving out column %s", column.getName()));
                continue;
            }
            String defaultValue = column.getDefaultValue();
            SimpleTypeDescriptor typeDescriptor = new SimpleTypeDescriptor(columnId, this, type);
            if (defaultValue != null)
                typeDescriptor.setDetailValue("constant", defaultValue);
            if (column.getSize() != null)
                typeDescriptor.setMaxLength(column.getSize());
            if (column.getFractionDigits() != null) {
            	if ("timestamp".equals(type))
            		typeDescriptor.setGranularity("1970-01-02");
            	else
            		typeDescriptor.setGranularity(decimalGranularity(column.getFractionDigits()));
            }
            //typeDescriptors.put(typeDescriptor.getName(), typeDescriptor);
            PartDescriptor descriptor = new PartDescriptor(columnName, this);
            descriptor.setLocalType(typeDescriptor);
            descriptor.setMinCount(new ConstantExpression<Long>(1L));
            descriptor.setMaxCount(new ConstantExpression<Long>(1L));
            descriptor.setNullable(column.getNotNullConstraint() == null);
            List<DBUniqueConstraint> ukConstraints = column.getUkConstraints();
            for (DBUniqueConstraint constraint : ukConstraints) {
                if (constraint.getColumnNames().length == 1) {
                    descriptor.setUnique(true);
                } else {
                    LOGGER.warn("Automated uniqueness assurance on multiple columns is not provided yet: " + constraint);
                    // TODO v0.7.6 support uniqueness constraints on combination of columns
                }
            }
            LOGGER.debug("parsed attribute " + columnId + ": " + descriptor);
            complexType.addComponent(descriptor);
        }
		return complexType;
	}

    List<ColumnInfo> getWriteColumnInfos(Entity entity, boolean insert) {
        String tableName = entity.type();
        DBTable table = getTable(tableName);
        List<String> pkColumnNames = CollectionUtil.toList(table.getPKColumnNames());
        ComplexTypeDescriptor typeDescriptor = (ComplexTypeDescriptor) getTypeDescriptor(tableName);
        Collection<ComponentDescriptor> componentDescriptors = typeDescriptor.getComponents();
        List<ColumnInfo> pkInfos = new ArrayList<ColumnInfo>(componentDescriptors.size());
        List<ColumnInfo> normalInfos = new ArrayList<ColumnInfo>(componentDescriptors.size());
        ComplexTypeDescriptor entityDescriptor = entity.descriptor();
        for (ComponentDescriptor dbCompDescriptor : componentDescriptors) {
            ComponentDescriptor enCompDescriptor = entityDescriptor.getComponent(dbCompDescriptor.getName());
            if (enCompDescriptor != null && enCompDescriptor.getMode() == Mode.ignored)
                continue;
            if (dbCompDescriptor.getMode() != Mode.ignored) {
                String name = dbCompDescriptor.getName();
                SimpleTypeDescriptor type = (SimpleTypeDescriptor) dbCompDescriptor.getTypeDescriptor();
				PrimitiveType primitiveType = type.getPrimitiveType();
				if (primitiveType == null) {
					if (!acceptUnknownColumnTypes)
						throw new ConfigurationError("Column type of " + entityDescriptor.getName() + "." + 
							dbCompDescriptor.getName() + " unknown: " + type.getName());
					else if (entity.get(type.getName()) instanceof String)
						primitiveType = PrimitiveType.STRING;
					else
						primitiveType = PrimitiveType.OBJECT;
				}
				String primitiveTypeName = primitiveType.getName();
                DBColumn column = table.getColumn(name);
                DBDataType columnType = column.getType();
                int sqlType = columnType.getJdbcType();
                Class<?> javaType = driverTypeMapper.concreteType(primitiveTypeName);
                ColumnInfo info = new ColumnInfo(name, sqlType, javaType);
                if (pkColumnNames.contains(name))
    				pkInfos.add(info);
                else
                	normalInfos.add(info);
            }
        }
        if (insert) {
        	pkInfos.addAll(normalInfos);
        	return pkInfos;
        } else {
        	normalInfos.addAll(pkInfos);
        	return normalInfos;
        }
    }

    DBTable getTable(String tableName) {
    	parseMetadataIfNecessary();
        DBTable table = findTableInConfiguredCatalogAndSchema(tableName);
        if (table != null)
            return table;
        table = findAnyTableOfName(tableName);
        if (table != null) {
           	LOGGER.warn("Table '" + tableName + "' not found " +
           			"in the expected catalog '" + catalogName + "' and schema '" + schemaName + "'. " +
   					"I have taken it from catalog '" + table.getCatalog() + "' and schema '" + table.getSchema() + "' instead. " +
   					"You better make sure this is right and fix the configuration");
            return table;
        }
        throw new ObjectNotFoundException("Table " + tableName);
    }

    private DBTable findAnyTableOfName(String tableName) {
        for (DBCatalog catalog : database.getCatalogs()) {
            for (DBSchema schema : catalog.getSchemas()) {
                DBTable table = schema.getTable(tableName);
                if (table != null)
                    return table;
            }
        }
        return null;
	}

	private DBTable findTableInConfiguredCatalogAndSchema(String tableName) {
        DBCatalog catalog = database.getCatalog(catalogName);
        if (catalog == null)
        	throw new ConfigurationError("Catalog '" + catalogName + "' not found in database '" + id + "'");
		DBSchema dbSchema = catalog.getSchema(schemaName);
        if (dbSchema != null) {
            DBTable table = dbSchema.getTable(tableName);
            if (table != null)
                return table;
        }
        return null;
	}

	private synchronized ThreadContext getThreadContext() {
        Thread currentThread = Thread.currentThread();
        ThreadContext context = threadContexts.get(currentThread);
        if (context == null) {
            context = new ThreadContext();
            threadContexts.put(currentThread, context);
        }
        return context;
    }
    
	private void persistOrUpdate(Entity entity, boolean insert) {
        parseMetadataIfNecessary();
        List<ColumnInfo> writeColumnInfos = getWriteColumnInfos(entity, insert);
        try {
            String tableName = entity.type();
            PreparedStatement statement = getStatement(entity.descriptor(), insert, writeColumnInfos);
            for (int i = 0; i < writeColumnInfos.size(); i++) {
            	ColumnInfo info = writeColumnInfos.get(i);
                Object componentValue = entity.getComponent(info.name);
                Object jdbcValue = componentValue;
                if (info.type != null)
                	jdbcValue = AnyConverter.convert(jdbcValue, info.type);
                try {
                    boolean criticalOracleType = (dialect instanceof OracleDialect && (info.sqlType == Types.NCLOB || info.sqlType == Types.OTHER));
					if (jdbcValue != null || criticalOracleType) // Oracle is not able to perform setNull() on NCLOBs and NVARCHAR2
                        statement.setObject(i + 1, jdbcValue);
                    else
                        statement.setNull(i + 1, info.sqlType);
                } catch (SQLException e) {
                    throw new RuntimeException("error setting column " + tableName + '.' + info.name, e);
                }
            }
            if (batch) {
                statement.addBatch();
            } else {
                int rowCount = statement.executeUpdate();
                if (rowCount == 0)
                	throw new RuntimeException("Update failed because, since there is no database entry with the PK of " + entity);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error in persisting " + entity, e);
        }
	}
	
	private void parseMetadataIfNecessary() {
	    if (typeDescriptors == null)
            parseMetaData();
    }

    private class ThreadContext {
        
        Connection connection;
        
        public Map<ComplexTypeDescriptor, PreparedStatement> insertStatements;
        public Map<ComplexTypeDescriptor, PreparedStatement> updateStatements;
        public Map<ComplexTypeDescriptor, PreparedStatement> selectByPKStatements;
        
        public ThreadContext() {
            insertStatements = new OrderedMap<ComplexTypeDescriptor, PreparedStatement>();
            updateStatements = new OrderedMap<ComplexTypeDescriptor, PreparedStatement>();
            selectByPKStatements = new OrderedMap<ComplexTypeDescriptor, PreparedStatement>();
            connection = createConnection();
        }
        
        void commit() {
            try {
				flushStatements(insertStatements);
				flushStatements(updateStatements);
                JDBC_LOGGER.debug("Committing connection: {}" + connection);
                connection.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

		private void flushStatements(Map<ComplexTypeDescriptor, PreparedStatement> statements) throws SQLException {
			for (Map.Entry<ComplexTypeDescriptor, PreparedStatement> entry : statements.entrySet()) {
			    PreparedStatement statement = entry.getValue();
			    if (statement != null) {
			        // need to finish old statement
		            if (batch)
		                statement.executeBatch();
		            JDBC_LOGGER.debug("Closing statement: {}", statement);
			        DBUtil.close(statement);
			    }
			    entry.setValue(null);
			}
		}

        public PreparedStatement getSelectByPKStatement(ComplexTypeDescriptor descriptor) {
            try {
                PreparedStatement statement = selectByPKStatements.get(descriptor);
                if (statement == null)
                    statement = createSelectByPKStatement(descriptor);
                else
                    statement.clearParameters();
                return statement;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

		private PreparedStatement createSelectByPKStatement(ComplexTypeDescriptor descriptor) throws SQLException {
	        PreparedStatement statement;
	        String tableName = descriptor.getName();
	        DBTable table = tables.get(tableName.toUpperCase());
	        if (table == null)
	        	throw new IllegalArgumentException("Table not found: " + tableName);
	        StringBuilder builder = new StringBuilder("select * from ").append(tableName).append(" where");
	        for (String idColumnName : descriptor.getIdComponentNames())
	        	builder.append(' ').append(idColumnName).append("=?");
	        statement = DBUtil.prepareStatement(connection, builder.toString(), readOnly);
        	selectByPKStatements.put(descriptor, statement);
	        return statement;
        }

        public PreparedStatement getStatement(ComplexTypeDescriptor descriptor, boolean insert, List<ColumnInfo> columnInfos) {
            try {
                PreparedStatement statement = (insert ? insertStatements.get(descriptor) : updateStatements.get(descriptor));
                if (statement == null)
                    statement = createStatement(descriptor, insert, columnInfos);
                else
                    statement.clearParameters();
                return statement;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

		private PreparedStatement createStatement(ComplexTypeDescriptor descriptor, boolean insert,
                List<ColumnInfo> columnInfos) throws SQLException {
	        PreparedStatement statement;
	        String tableName = descriptor.getName();
	        DBTable table = tables.get(tableName.toUpperCase());
	        if (table == null)
	        	throw new IllegalArgumentException("Table not found: " + tableName);
	        String sql = (insert ? 
	        		dialect.insert(table, columnInfos) : 
	        		dialect.update(table, getTable(tableName).getPKColumnNames(), columnInfos));
            JDBC_LOGGER.debug("Creating prepared statement: {}", sql);
	        statement = DBUtil.prepareStatement(connection, sql, readOnly);
	        if (insert)
	        	insertStatements.put(descriptor, statement);
	        else
	        	updateStatements.put(descriptor, statement);
	        return statement;
        }

        public void close() {
            commit();
            DBUtil.close(connection);
        }
    }

    private String decimalGranularity(int scale) {
        if (scale == 0)
            return "1";
        StringBuilder builder = new StringBuilder("0.");
        for (int i = 1; i < scale; i++)
            builder.append('0');
        builder.append(1);
        return builder.toString();
    }

    private TypeMapper driverTypeMapper() {
        return new TypeMapper(
                "byte",        Byte.class,
                "short",       Short.class,
                "int",         Integer.class,
                "big_integer", Long.class,
                "float",       Float.class,
                "double",      Double.class,
                "big_decimal", BigDecimal.class,
                
                "boolean",     Boolean.class,
                "char",        Character.class,
                "date",        java.sql.Date.class,
                "timestamp",   java.sql.Timestamp.class,
                
                "string",      java.sql.Clob.class,
                "string",      String.class,
                
                "binary",      Blob.class,
                "binary",      byte[].class
                
//              "object",      Object.class,
                
        );
    }

}

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

package org.databene.benerator.engine.statement;

import java.io.IOException;
import java.io.StreamTokenizer;
import static java.io.StreamTokenizer.*;
import java.io.StringReader;
import java.util.List;

import org.databene.benerator.composite.ComponentAndVariableSupport;
import org.databene.benerator.composite.GeneratorComponent;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.factory.ComplexTypeGeneratorFactory;
import org.databene.commons.ArrayBuilder;
import org.databene.commons.ArrayFormat;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.IOUtil;
import org.databene.commons.SyntaxError;
import org.databene.jdbacl.SQLUtil;
import org.databene.jdbacl.identity.IdentityModel;
import org.databene.jdbacl.identity.IdentityProvider;
import org.databene.jdbacl.identity.KeyMapper;
import org.databene.jdbacl.identity.NoIdentity;
import org.databene.jdbacl.model.DBForeignKeyConstraint;
import org.databene.jdbacl.model.DBTable;
import org.databene.jdbacl.model.Database;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.ReferenceDescriptor;
import org.databene.model.data.Uniqueness;
import org.databene.platform.db.DBSystem;
import org.databene.webdecs.DataContainer;
import org.databene.webdecs.DataIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cascades the 'transcode' operation to all entities configured to be related 
 * to the currently transcoded entity.<br/><br/>
 * Created: 18.04.2011 07:14:34
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class CascadeStatement extends SequentialStatement implements CascadeParent {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CascadeStatement.class);
	
	private static final String REF_SYNTAX_MESSAGE = "Expected Syntax: table(column1, column2, ...)";

	private CascadeParent parent;
	private Reference ref;
	private Entity currentEntity;
	MutatingTypeExpression typeExpression;
	ComplexTypeDescriptor type;

	public CascadeStatement(String ref, MutatingTypeExpression typeExpression, CascadeParent parent) {
	    this.typeExpression = typeExpression;
		this.ref = Reference.parse(ref);
		this.parent = parent;
		this.currentEntity = null;
	}

	@Override
	public boolean execute(BeneratorContext context) {
		DBSystem source = getSource(context);
		getType(source, context);
		IdentityModel identity = parent.getIdentityProvider().getIdentity(type.getName(), false);
		String tableName = type.getName();
		LOGGER.debug("Cascading transcode from " + parent.currentEntity().type() + " to " + tableName);
		
		// iterate rows
    	List<GeneratorComponent<Entity>> generatorComponents = 
    		ComplexTypeGeneratorFactory.createMutatingGeneratorComponents(type, Uniqueness.NONE, context);
        ComponentAndVariableSupport<Entity> cavs = new ComponentAndVariableSupport<Entity>(tableName, 
        		generatorComponents, context);
        cavs.init(context);

        DataIterator<Entity> iterator = ref.resolveReferences(parent.currentEntity(), source, context);
        DataContainer<Entity> container = new DataContainer<Entity>();
		while ((container = iterator.next(container)) != null)
			mutateAndTranscodeEntity(container.getData(), identity, cavs, context);
		IOUtil.close(iterator);
    	return true;
	}

	public DBSystem getSource(BeneratorContext context) {
		return parent.getSource(context);
	}

	public Entity currentEntity() {
		return currentEntity;
	}

	public KeyMapper getKeyMapper() {
		return parent.getKeyMapper();
	}

	public IdentityProvider getIdentityProvider() {
		return parent.getIdentityProvider();
	}

	public boolean needsNkMapping(String type) {
		return parent.needsNkMapping(type);
	}

	public DBSystem getTarget(BeneratorContext context) {
		return parent.getTarget(context);
	}

	public ComplexTypeDescriptor getType(DBSystem db, BeneratorContext context) {
		if (type == null) {
			String parentType = parent.getType(db, context).getName();
			typeExpression.setTypeName(ref.getTargetTableName(parentType, db, context));
			type = typeExpression.evaluate(context);
		}
		return type;
	}

	// implementation --------------------------------------------------------------------------------------------------
	
	private void mutateAndTranscodeEntity(Entity sourceEntity, IdentityModel identity, ComponentAndVariableSupport<Entity> cavs, BeneratorContext context) {
    	Object sourcePK = sourceEntity.idComponentValues();
    	boolean mapNk = parent.needsNkMapping(sourceEntity.type());
    	String nk = null;
    	KeyMapper mapper = getKeyMapper();
    	DBSystem source = getSource(context);
		if (mapNk)
    		nk = mapper.getNaturalKey(source.getId(), identity, sourcePK);
		Entity targetEntity = new Entity(sourceEntity);
		cavs.apply(targetEntity, context);
    	Object targetPK = targetEntity.idComponentValues();
		transcodeForeignKeys(targetEntity, source, context);
		mapper.store(source.getId(), identity, nk, sourcePK, targetPK);
	    getTarget(context).store(targetEntity);
        LOGGER.debug("transcoded {} to {}", sourceEntity, targetEntity);
        cascade(sourceEntity, context);
	}

	private void transcodeForeignKeys(Entity entity, DBSystem source, Context context) {
		ComplexTypeDescriptor tableDescriptor = entity.descriptor();
		for (InstanceDescriptor component : tableDescriptor.getParts()) {
			if (component instanceof ReferenceDescriptor) {
				ReferenceDescriptor fk = (ReferenceDescriptor) component;
				String refereeTableName = fk.getTargetType();
				Object sourceRef = entity.get(fk.getName());
				if (sourceRef != null) {
					IdentityProvider identityProvider = parent.getIdentityProvider();
					IdentityModel sourceIdentity = identityProvider.getIdentity(refereeTableName, false);
					if (sourceIdentity == null) {
						DBTable refereeTable = source.getDbMetaData().getTable(refereeTableName);
						sourceIdentity = new NoIdentity(refereeTable.getName());
						identityProvider.registerIdentity(sourceIdentity, refereeTableName);
					}
						
					boolean needsNkMapping = parent.needsNkMapping(refereeTableName);
					if (sourceIdentity instanceof NoIdentity && needsNkMapping)
						throw new ConfigurationError("No identity defined for table " + refereeTableName);
					KeyMapper mapper = parent.getKeyMapper();
					Object targetRef;
					if (needsNkMapping) {
						String sourceRefNK = mapper.getNaturalKey(source.getId(), sourceIdentity, sourceRef);
						targetRef = mapper.getTargetPK(sourceIdentity, sourceRefNK);
					} else {
						targetRef = mapper.getTargetPK(source.getId(), sourceIdentity, sourceRef);
					}
					if (targetRef == null) {
						String message = "No mapping found for " + source.getId() + '.' + refereeTableName + "#" + sourceRef + 
								" referred in " + entity.type() + "(" + fk.getName() + "). " +
								"Probably has not been in the result set of the former '" + refereeTableName + "' nk query.";
						getErrorHandler(context).handleError(message);
					}
					entity.set(fk.getName(), targetRef);
				}
			}
		}
	}

	private void cascade(Entity sourceEntity, BeneratorContext context) {
		this.currentEntity = sourceEntity;
		executeSubStatements(context);
		this.currentEntity = null;
	}

	public static class Reference {

		private String refererTableName;
		private String[] columnNames;
		
		private DBForeignKeyConstraint fk;
		private Database database;
		private DBTable refererTable;
		private DBTable refereeTable;
		private DBTable targetTable;

		public Reference(String refererTableName, String[] columnNames) {
			this.refererTableName = refererTableName;
			this.columnNames = columnNames;
		}
		
		public String getTargetTableName(String parentTable, DBSystem db, BeneratorContext context) {
			if (!parentTable.equals(refererTableName))
				return refererTableName;
			else {
				initIfNecessary(parentTable, db, context);
				return targetTable.getName();
			}
		}

		public DataIterator<Entity> resolveReferences(Entity currentEntity, DBSystem db, BeneratorContext context) {
			initIfNecessary(currentEntity.type(), db, context);
			DBTable parentTable = database.getTable(currentEntity.type());
			if (parentTable.equals(refereeTable))
				return resolveToManyReference(currentEntity, fk, db, context); // including self-recursion
			else if (parentTable.equals(refererTable))
				return resolveToOneReference(currentEntity, fk, db, context);
			else
				throw new ConfigurationError("Table '" + parentTable + "' does not relate to the foreign key " + 
						refererTableName + '(' + ArrayFormat.format(columnNames) + ')');
		}
		
		private void initIfNecessary(String parentTable, DBSystem db, BeneratorContext context) {
			if (this.database != null)
				return;
			this.database = db.getDbMetaData();
			this.refererTable = this.database.getTable(refererTableName);
			this.fk = refererTable.getForeignKeyConstraint(columnNames);
			this.refereeTable = fk.getRefereeTable();
			this.targetTable = (parentTable.equalsIgnoreCase(refereeTable.getName()) ? refererTable : refereeTable);
		}

		DataIterator<Entity> resolveToManyReference(
				Entity fromEntity, DBForeignKeyConstraint fk, DBSystem db, BeneratorContext context) {
			StringBuilder selector = new StringBuilder();
			String[] refererColumnNames = fk.getColumnNames();
			String[] refereeColumnNames = fk.getRefereeColumnNames();
			for (int i = 0; i < refererColumnNames.length; i++) {
				if (selector.length() > 0)
					selector.append(" and ");
				Object refereeColumnValue = fromEntity.get(refereeColumnNames[i]);
				selector.append(refererColumnNames[i]).append('=').append(SQLUtil.renderValue(refereeColumnValue));
			}
			return db.queryEntities(fk.getTable().getName(), selector.toString(), context).iterator();
		}

		DataIterator<Entity> resolveToOneReference(
				Entity fromEntity, DBForeignKeyConstraint fk, DBSystem db, BeneratorContext context) {
			StringBuilder selector = new StringBuilder();
			String[] refererColumnNames = fk.getColumnNames();
			String[] refereeColumnNames = fk.getRefereeColumnNames();
			for (int i = 0; i < refererColumnNames.length; i++) {
				if (selector.length() > 0)
					selector.append(" and ");
				Object refererColumnValue = fromEntity.get(refererColumnNames[i]);
				selector.append(refereeColumnNames[i]).append('=').append(SQLUtil.renderValue(refererColumnValue));
			}
			return db.queryEntities(fk.getRefereeTable().getName(), selector.toString(), context).iterator();
		}
		
		static Reference parse(String refSpec) {
			StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(refSpec));
			tokenizer.wordChars('_', '_');
			try {
				// parse table name
				int token = tokenizer.nextToken();
				if (token != TT_WORD)
					throw new SyntaxError(REF_SYNTAX_MESSAGE, refSpec);
				String tableName = tokenizer.sval;
				
				// parse column names
				if ((token = tokenizer.nextToken()) != '(')
					throw new SyntaxError(REF_SYNTAX_MESSAGE, refSpec);
				ArrayBuilder<String> columnNames = new ArrayBuilder<String>(String.class);
				do {
					if ((token = tokenizer.nextToken()) != TT_WORD)
						throw new SyntaxError(REF_SYNTAX_MESSAGE, refSpec);
					columnNames.add(tokenizer.sval);
					token = tokenizer.nextToken();
					if (token != ',' && token != ')')
						throw new SyntaxError(REF_SYNTAX_MESSAGE, refSpec);
				} while (token == ',');
				if (token != ')')
					throw new SyntaxError("reference definition must end with ')'", refSpec);
				return new Reference(tableName, columnNames.toArray());
			} catch (IOException e) {
				throw new SyntaxError(REF_SYNTAX_MESSAGE, refSpec);
			}
		}

	}

}

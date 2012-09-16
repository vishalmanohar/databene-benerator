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

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.util.ThreadSafeNonNullGenerator;
import org.databene.commons.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract parent class for database-sequence-related {@link Generator}s.<br/><br/>
 * Created: 24.07.2011 06:16:59
 * @since 0.7.0
 * @author Volker Bergmann
 */
public abstract class AbstractSequenceGenerator extends ThreadSafeNonNullGenerator<Long> {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected String name;
	protected DBSystem database;

    public AbstractSequenceGenerator(String name, DBSystem database) {
	    this.name = name;
	    this.database = database;
    }
    
    // properties ------------------------------------------------------------------------------------------------------
    
	public String getName() {
    	return name;
    }

	public void setName(String name) {
    	this.name = name;
    }

	public DBSystem getDatabase() {
    	return database;
    }

	public void setDatabase(DBSystem database) {
    	this.database = database;
    }
	
	// Generator interface implementation ------------------------------------------------------------------------------

	public Class<Long> getGeneratedType() {
	    return Long.class;
    }

	@Override
	public synchronized void init(GeneratorContext context) {
	    if (database == null)
	    	throw new InvalidGeneratorSetupException("No 'source' database defined");
	    if (StringUtil.isEmpty(name))
	    	throw new InvalidGeneratorSetupException("No sequence 'name' defined");
		super.init(context);
	}
	
    // helpers ---------------------------------------------------------------------------------------------------------

	protected long fetchSequenceValue() {
		return database.nextSequenceValue(name);
	}

}

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

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.Statement;
import org.databene.commons.ConfigurationError;
import org.databene.platform.memstore.MemStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Statement} that instantiates a {@link MemStore} 
 * and registers it in the {@link BeneratorContext}.<br/><br/>
 * Created: 08.03.2011 13:30:45
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class MemStoreStatement implements Statement {
	
	private static Logger logger = LoggerFactory.getLogger(DefineDatabaseStatement.class);
	
	private String id;
	ResourceManager resourceManager;
	
	public MemStoreStatement(String id, ResourceManager resourceManager) {
		if (id == null)
			throw new ConfigurationError("No store id defined");
		this.id = id;
		this.resourceManager = resourceManager;
    }

    public boolean execute(BeneratorContext context) {
	    logger.debug("Instantiating store with id '" + id + "'");
		MemStore store = new MemStore(id, context.getDataModel());
	    // register this object on all relevant managers and in the context
	    context.set(id, store);
	    context.getDataModel().addDescriptorProvider(store);
	    resourceManager.addResource(store);
    	return true;
    }

}

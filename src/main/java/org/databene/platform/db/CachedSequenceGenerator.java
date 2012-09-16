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

package org.databene.platform.db;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

import org.databene.benerator.GeneratorContext;

/**
 * Reads the current value of a sequence on first invocation, 
 * increases the value locally on subsequent calls and 
 * finally (on close()) updates the DB sequence with the local value.
 * This saves database round trips but limits execution to a single 
 * client.<br/><br/>
 * Created: 11.11.2009 18:35:26
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class CachedSequenceGenerator extends AbstractSequenceGenerator {

	private AtomicLong cacheValue;
	
	public CachedSequenceGenerator() {
		this(null, null);
    }

	public CachedSequenceGenerator(String name, DBSystem database) {
		super(name, database);
    }
	
	@Override
	public void init(GeneratorContext context) {
	    super.init(context);
		cacheValue = new AtomicLong(fetchSequenceValue());
	}

	@Override
	public Long generate() {
		return cacheValue.getAndIncrement();
    }
	
	@Override
	public void close() {
		try {
    		database.setSequenceValue(name, cacheValue.get());
    		cacheValue = null;
    		super.close();
		} catch (SQLException e) {
			logger.error("Error closing " + this, e);
		}
	}

}

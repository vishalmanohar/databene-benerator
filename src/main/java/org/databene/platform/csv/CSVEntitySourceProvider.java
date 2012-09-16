/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.csv;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.factory.DataSourceProvider;
import org.databene.commons.Converter;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.EntitySource;

/**
 * {@link EntitySource} implementation which creates {@link Iterable}s that iterate through CSV files.<br/><br/>
 * Created: 05.05.2010 14:52:01
 * @since 0.6.1
 * @author Volker Bergmann
 */
public class CSVEntitySourceProvider implements DataSourceProvider<Entity> {
	
	private ComplexTypeDescriptor entityType;
	private Converter<String, ?> converter;
	private char separator;
	private String encoding;
	
	public CSVEntitySourceProvider(ComplexTypeDescriptor entityType, Converter<String, ?> converter, char separator, String encoding) {
	    this.entityType = entityType;
	    this.converter = converter;
	    this.separator = separator;
	    this.encoding = encoding;
    }

	public EntitySource create(String id, BeneratorContext context) {
		CSVEntitySource iterable = new CSVEntitySource(id, entityType, encoding, converter, separator);
		iterable.setContext(context);
		return iterable;
    }

}

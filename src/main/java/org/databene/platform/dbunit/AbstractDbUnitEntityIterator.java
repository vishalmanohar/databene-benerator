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

package org.databene.platform.dbunit;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.commons.IOUtil;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.webdecs.DataIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract parent class for classes that iterate DbUnit files and provide their content as {@link Entity}.<br/><br/>
 * Created: 20.09.2011 08:07:44
 * @since 0.7.1
 * @author Volker Bergmann
 */
public abstract class AbstractDbUnitEntityIterator implements DataIterator<Entity> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected BeneratorContext context;
    
    protected XMLStreamReader reader;

    public AbstractDbUnitEntityIterator(String uri, BeneratorContext context) {
        try {
			this.context = context;
			XMLInputFactory factory = XMLInputFactory.newInstance();
			reader = factory.createXMLStreamReader(IOUtil.getInputStreamForURI(uri));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
    
    // DataIterator interface implementation ---------------------------------------------------------------------------

    public Class<Entity> getType() {
    	return Entity.class;
    }
    
    public void close() {
    	if (reader != null) {
    		try {
				reader.close();
			} catch (XMLStreamException e) {
				logger.warn("Error closing XML reader", e);
			}
    	}
        this.reader = null;
    }

    // non-public helpers ----------------------------------------------------------------------------------------------
    
    protected ComplexTypeDescriptor getType(Row row) {
        String name = row.getTableName();
        ComplexTypeDescriptor type = (ComplexTypeDescriptor) context.getDataModel().getTypeDescriptor(name);
        if (type == null)
            type = new ComplexTypeDescriptor(name, context.getLocalDescriptorProvider());
        return type;
    }

}

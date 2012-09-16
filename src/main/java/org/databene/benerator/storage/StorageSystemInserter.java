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

package org.databene.benerator.storage;

import org.databene.benerator.Consumer;
import org.databene.benerator.StorageSystem;
import org.databene.benerator.composite.EntityTypeChanger;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;

/**
 * {@link Consumer} implementation that inserts entities into database tables.<br/><br/>
 * Created: 02.08.2010 19:38:56
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class StorageSystemInserter extends StorageSystemConsumer {

    private ComplexTypeDescriptor targetType;

    public StorageSystemInserter(StorageSystem system) {
        this(system, null);
    }

    public StorageSystemInserter(StorageSystem system, ComplexTypeDescriptor targetType) {
    	super(system);
        this.targetType = targetType;
    }

    @Override
	public void startProductConsumption(Object object) {
    	Entity entity = (Entity) object;
    	if (targetType == null)
    		system.store(entity);
    	else
    		system.store(EntityTypeChanger.changeType(entity, targetType));
    }

}

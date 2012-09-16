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

package org.databene.benerator.composite;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import org.databene.BeneratorConstants;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.MessageHolder;
import org.databene.commons.Resettable;
import org.databene.commons.ThreadAware;
import org.databene.commons.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Offers support for entity or array component generation with or without variable generation.<br/><br/>
 * Created: 13.01.2011 10:52:43
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class ComponentAndVariableSupport<E> implements ThreadAware, MessageHolder, Resettable, Closeable {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentAndVariableSupport.class);
    private static final Logger STATE_LOGGER = LoggerFactory.getLogger(BeneratorConstants.STATE_LOGGER);
    
    private String instanceName;
    private List<GeneratorComponent<E>> components;
	private String message;
	
	public ComponentAndVariableSupport(String instanceName, List<GeneratorComponent<E>> components, 
			GeneratorContext context) {
		this.instanceName = instanceName;
        this.components = (components != null ? components : new ArrayList<GeneratorComponent<E>>());
	}
	
    public void init(BeneratorContext context) {
    	for (GeneratorComponent<?> component : components)
    		component.init(context);
	}

    public boolean apply(E target, BeneratorContext context) {
    	BeneratorContext subContext = context.createSubContext();
    	subContext.setCurrentProduct(new ProductWrapper<E>(target));
    	for (GeneratorComponent<E> component : components) {
            try {
                if (!component.execute(subContext)) {
                	message = "Component generator for '" + instanceName + 
                		"' is not available any longer: " + component;
                    STATE_LOGGER.debug(message);
                    return false;
                }
            } catch (Exception e) {
                throw new RuntimeException("Failure in generation of '" + instanceName + "', " +
                		"Failed component: " + component, e);
            }
    	}
    	LOGGER.debug("Generated {}", target);
    	subContext.close();
    	return true;
	}

    public void reset() {
		for (GeneratorComponent<E> component : components)
			component.reset();
	}

    public void close() {
		for (GeneratorComponent<E> component : components)
			component.close();
	}
	
	public String getMessage() {
		return message;
	}
	
	
	
	// ThreadAware interface implementation ----------------------------------------------------------------------------
	
    public boolean isParallelizable() {
	    return ThreadUtil.allParallelizable(components);
    }

    public boolean isThreadSafe() {
	    return ThreadUtil.allThreadSafe(components);
    }
    
    
    
	// java.lang.Object overrides --------------------------------------------------------------------------------------
	
	@Override
	public String toString() {
	    return getClass().getSimpleName() + components;
	}

}

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

package org.databene.benerator.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.databene.benerator.Generator;
import org.databene.benerator.util.AbstractGenerator;
import org.databene.commons.Converter;
import org.databene.commons.ThreadAware;
import org.databene.commons.ThreadUtil;
import org.databene.commons.context.ContextAware;

/**
 * {@link Generator} implementation that makes use of other {@link ContextAware}
 * objects by which its threading support is influenced.<br/><br/>
 * Created: 20.03.2010 11:19:11
 * @since 0.6.0
 * @author Volker Bergmann
 */
public abstract class CompositeGenerator<E> extends AbstractGenerator<E> {
	
	protected Class<E> generatedType;
	protected List<ThreadAware> components;
	
	protected CompositeGenerator(Class<E> generatedType) {
		this.generatedType = generatedType;
		this.components = new ArrayList<ThreadAware>();
	}
	
	
	// component registration ------------------------------------------------------------------------------------------
	
	protected <T extends Generator<U>, U> T registerComponent(T component) {
		components.add(component);
		return component;
	}

	protected <T extends Converter<U,V>, U, V> T registerComponent(T component) {
		components.add(component);
		return component;
	}

	protected void registerComponents(ThreadAware[] components) {
		for (ThreadAware component : components)
			this.components.add(component);
	}

	
	
	// partial Generator interface implementation ----------------------------------------------------------------------
	
	public Class<E> getGeneratedType() {
	    return generatedType;
    }

	public boolean isThreadSafe() {
		return ThreadUtil.allThreadSafe(components);
    }

	public boolean isParallelizable() {
		return ThreadUtil.allParallelizable(components);
    }

}

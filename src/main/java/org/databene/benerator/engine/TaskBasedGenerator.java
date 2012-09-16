/*
 * (c) Copyright 2011-2012 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.engine.statement.GenerateAndConsumeTask;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.ErrorHandler;

/**
 * Wraps a {@link GenerateAndConsumeTask} with a {@link Generator} interface.<br/><br/>
 * Created: 01.09.2011 15:33:34
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class TaskBasedGenerator implements Generator<Object> {
	
	private GenerateAndConsumeTask task;
	private GeneratorContext context;
	private ErrorHandler errorHandler;
	private boolean initialized;

	public TaskBasedGenerator(GenerateAndConsumeTask task) {
		this.task = task;
		this.errorHandler = ErrorHandler.getDefault();
	}

	public boolean isParallelizable() {
		return task.isParallelizable();
	}

	public boolean isThreadSafe() {
		return task.isThreadSafe();
	}

	public Class<Object> getGeneratedType() {
		return Object.class;
	}

	public void init(GeneratorContext context) {
		task.init((BeneratorContext) context);
		this.context = context;
		this.initialized = true;
	}

	public boolean wasInitialized() {
		return initialized;
	}

	public ProductWrapper<Object> generate(ProductWrapper<Object> wrapper) {
		task.execute(context, errorHandler);
		ProductWrapper<?> currentProduct = task.getRecentProduct();
		if (currentProduct == null)
			return null;
		return new ProductWrapper<Object>().wrap(currentProduct.unwrap());
	}

	public void reset() {
		task.reset();
	}

	public void close() {
		task.close();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + '[' + task + ']';
	}
	
}

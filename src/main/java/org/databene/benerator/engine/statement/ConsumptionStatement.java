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

import org.databene.benerator.Consumer;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.wrapper.ProductWrapper;

/**
 * {@link Statement} that consumes the current entity of a {@link GeneratorContext} using a {@link Consumer}.<br/><br/>
 * Created: 01.09.2011 15:51:27
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class ConsumptionStatement implements Statement {
	
	private Consumer consumer;
	private boolean start;
	private boolean finish;
	
	public ConsumptionStatement(Consumer consumer, boolean start, boolean finish) {
		this.consumer = consumer;
		this.start = start;
		this.finish = finish;
	}

	public boolean execute(BeneratorContext context) {
		if (consumer != null) {
			ProductWrapper<?> product = context.getCurrentProduct();
			if (start)
				consumer.startConsuming(product);
			if (finish)
				consumer.finishConsuming(product);
		}
		return true;
	}

}

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

package org.databene.benerator.wrapper;

import org.databene.benerator.SequenceTestGenerator;
import org.databene.benerator.sample.OneShotGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.junit.Test;

/**
 * Tests the {@link GeneratorChain} class.<br/><br/>
 * Created: 22.07.2011 15:02:07
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class GeneratorChainTest extends GeneratorTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testUnique() {
		GeneratorChain<Integer> chain = new GeneratorChain<Integer>(Integer.class, true, 
				new SequenceTestGenerator<Integer>(2, 3),
				new SequenceTestGenerator<Integer>(1, 2));
		chain.init(context);
		expectGeneratedSequence(chain, 2, 3, 1).withCeasedAvailability();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testNonUnique() {
		GeneratorChain<Integer> chain = new GeneratorChain<Integer>(Integer.class, false, 
				new OneShotGenerator<Integer>(2),
				new OneShotGenerator<Integer>(1));
		chain.init(context);
		expectGeneratedSequence(chain, 2, 1).withCeasedAvailability();
	}
	
}

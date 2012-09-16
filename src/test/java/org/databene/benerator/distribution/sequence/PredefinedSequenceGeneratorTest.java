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

package org.databene.benerator.distribution.sequence;

import org.databene.benerator.test.GeneratorTest;
import org.junit.Test;

/**
 * Tests the {@link PredefinedSequenceGenerator}.<br/><br/>
 * Created: 03.06.2010 09:03:32
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class PredefinedSequenceGeneratorTest extends GeneratorTest {

	@Test
	public void testEmpty() {
		PredefinedSequenceGenerator<Integer> generator = new PredefinedSequenceGenerator<Integer>();
		expectGeneratedSequence(generator);
	}
	
	@Test
	public void testPrimes() {
		PredefinedSequenceGenerator<Integer> generator = new PredefinedSequenceGenerator<Integer>(2, 3, 5, 7, 11);
		expectGeneratedSequence(generator, 2, 3, 5, 7, 11);
	}
	
}

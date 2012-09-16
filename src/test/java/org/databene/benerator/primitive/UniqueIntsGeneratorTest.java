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

package org.databene.benerator.primitive;

import org.databene.benerator.test.GeneratorTest;
import org.junit.Test;

/**
 * Tests the {@link UniqueIntsGenerator}.<br/><br/>
 * Created: 01.08.2011 17:17:54
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class UniqueIntsGeneratorTest extends GeneratorTest {

	@Test
	public void test() {
		UniqueIntsGenerator generator = new UniqueIntsGenerator(2, 3);
		initialize(generator);
		expectGeneratedSequence(generator, 
			new int[] { 0, 1, 0 },
			new int[] { 1, 0, 1 },
			new int[] { 0, 0, 0 },
			new int[] { 1, 1, 1 },
			new int[] { 0, 1, 1 },
			new int[] { 1, 0, 0 },
			new int[] { 0, 0, 1 },
			new int[] { 1, 1, 0 }
		).withCeasedAvailability();
		close(generator);
	}

}

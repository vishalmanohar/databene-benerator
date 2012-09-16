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

package org.databene.domain.organization;

import static org.databene.benerator.util.GeneratorUtil.*;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;
import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.wrapper.ProductWrapper;
import org.junit.Test;

/**
 * Tests the {@link LegalFormGenerator}.<br/><br/>
 * Created: 24.08.2011 00:45:53
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class LegalFormGeneratorTest extends GeneratorTest {

	@Test
	public void testDE() {
		LegalFormGenerator generator = new LegalFormGenerator("DE");
		init(generator);
		Set<String> generatedSet = generatedSet(generator, 200);
		assertTrue(generatedSet.contains("GmbH"));
		assertFalse(generatedSet.contains("llc"));
		close(generator);
	}

	protected static Set<String> generatedSet(LegalFormGenerator generator, int n) {
		Set<String> result = new HashSet<String>();
		ProductWrapper<String> wrapper = new ProductWrapper<String>();
		for (int i = 0; i < n; i++)
			result.add(generator.generate(wrapper ).unwrap());
		return result;
	}
	
}

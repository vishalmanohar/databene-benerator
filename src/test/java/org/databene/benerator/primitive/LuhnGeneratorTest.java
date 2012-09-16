/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.test.GeneratorClassTest;
import org.databene.benerator.util.LuhnValidator;
import org.databene.commons.validator.RegexValidator;
import org.junit.Test;

/**
 * Tests the {@link LuhnGenerator}.<br/><br/>
 * Created: 18.10.2009 10:26:47
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class LuhnGeneratorTest extends GeneratorClassTest {

	public LuhnGeneratorTest() {
	    super(LuhnGenerator.class);
    }

	@Test
	public void testDefault() {
		expectGenerations(initialize(new LuhnGenerator()), 100, new LuhnValidator());
	}
	
	@Test
	public void testPrefix() {
		expectGenerations(initialize(new LuhnGenerator("123", 4, 8, 4, null)), 100, 
				new LuhnValidator(), 
				new RegexValidator("123[0-9]{1,5}"));
	}
	
}

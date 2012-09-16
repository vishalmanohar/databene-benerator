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

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.databene.benerator.Generator;
import org.databene.benerator.SequenceTestGenerator;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.benerator.util.GeneratorUtil;
import org.junit.Test;

/**
 * Tests the {@link AsBigDecimalGeneratorWrapper}.<br/><br/>
 * Created: 12.01.2011 00:02:55
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class AsBigDecimalGeneratorWrapperTest {

	@Test
	public void testGranularity() {
		Generator<Double> source = new SequenceTestGenerator<Double>(0.1234, 1.234, 12.34, 123.4, 1234.56);
		AsBigDecimalGeneratorWrapper<Double> wrapper 
			= new AsBigDecimalGeneratorWrapper<Double>(source, BigDecimal.ZERO, new BigDecimal("0.01"));
		wrapper.init(new DefaultBeneratorContext());
		assertEquals(new BigDecimal("0.12"), GeneratorUtil.generateNonNull(wrapper));
		assertEquals(new BigDecimal("1.23"), GeneratorUtil.generateNonNull(wrapper));
		assertEquals(new BigDecimal("12.34"), GeneratorUtil.generateNonNull(wrapper));
		assertEquals(new BigDecimal("123.40"), GeneratorUtil.generateNonNull(wrapper));
		assertEquals(new BigDecimal("1234.56"), GeneratorUtil.generateNonNull(wrapper));
	}
	
}

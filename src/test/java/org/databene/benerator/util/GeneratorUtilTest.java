/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.util;

import static org.junit.Assert.*;

import java.util.List;

import org.databene.benerator.Generator;
import org.databene.benerator.SequenceTestGenerator;
import org.databene.benerator.engine.BeneratorOpts;
import org.databene.benerator.primitive.IncrementGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.databene.commons.SysUtil;
import org.junit.Test;

/**
 * Tests the {@link GeneratorUtil} class.<br/><br/>
 * Created: 30.07.2010 18:54:13
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class GeneratorUtilTest extends GeneratorTest {

	@Test
	public void testAllProducts_defaultCacheSize() {
		Generator<Long> source = new IncrementGenerator(1, 1, 120000);
		source.init(context);
		List<Long> products = GeneratorUtil.allProducts(source);
		assertEquals(100000, products.size());
		assertEquals(1L, products.get(0).longValue());
		assertEquals(100000L, products.get(99999).longValue());
	}
	
	@Test
	public void testAllProducts_cacheSizeOverride() {
		SysUtil.runWithSystemProperty(BeneratorOpts.OPTS_CACHE_SIZE, "2", new Runnable() {
			public void run() {
				SequenceTestGenerator<Integer> source = new SequenceTestGenerator<Integer>(1, 2, 3, 4);
				source.init(context);
				List<Integer> products = GeneratorUtil.allProducts(source);
				assertEquals(2, products.size());
				assertEquals(1, products.get(0).intValue());
				assertEquals(2, products.get(1).intValue());
            }
		});
	}
	
}

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

package org.databene.benerator.factory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import org.databene.benerator.Generator;
import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.Encodings;
import org.junit.Test;

/**
 * Tests the {@link SourceFactory}.<br/><br/>
 * Created: 06.08.2011 13:13:19
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class SourceFactoryTest extends GeneratorTest {

    @Test
    public void testGetCSVCellGenerator() {
        Generator<String> generator = SourceFactory.createCSVCellGenerator("file://org/databene/csv/names-abc.csv", ',', Encodings.UTF_8);
        generator.init(context);
        assertEquals("Alice", nextProduct(generator));
        assertEquals("Bob", nextProduct(generator));
        assertEquals("Charly", nextProduct(generator));
        assertNull(generator.generate(new ProductWrapper<String>()));
    }

    @Test
    public void testGetArraySourceGenerator() {
        Generator<String[]> generator = SourceFactory.createCSVLineGenerator(
                "file://org/databene/csv/names-abc.csv", ',', Encodings.UTF_8, true);
        generator.init(context);
        assertEqualArrays(new String[] { "Alice", "Bob" }, nextProduct(generator));
        assertEqualArrays(new String[] { "Charly"}, nextProduct(generator));
        assertNull(generator.generate(new ProductWrapper<String[]>()));
    }

	protected <T> T nextProduct(Generator<T> generator) {
		return generator.generate(new ProductWrapper<T>()).unwrap();
	}

}

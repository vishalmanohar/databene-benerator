/*
 * (c) Copyright 2007-2011 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
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

package org.databene.benerator.test;

import org.databene.benerator.Generator;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.ParseUtil;
import junit.framework.AssertionFailedError;
import org.junit.Test;

/**
 * Provides methods for testing generators and standard tests that act on generically created generator instances.<br/>
 * <br/>
 * Created: 13.11.2007 13:13:07
 * @author Volker Bergmann
 */
public abstract class GeneratorClassTest extends GeneratorTest {

    @SuppressWarnings("rawtypes")
	protected Class<? extends Generator> generatorClass;

    @SuppressWarnings("rawtypes")
	public GeneratorClassTest(Class<? extends Generator> generatorClass) {
        this.generatorClass = generatorClass;
    }

    // test methods that apply for all Generators ----------------------------------------------------------------------

    @Test
    public void testDefaultConstructor() throws Throwable {
        generatorClass.newInstance();
    }

    @Test
    public void testToString() throws Throwable {
        Generator<?> generator = generatorClass.newInstance();
        assertCustomToStringMethod(generator);
        try {
	        initialize(generator);
        } catch (Exception e) {
        	// if the default instance is invalid, further tests make no sense
	        return;
        }
        generator.toString();
        generator.close();
        generator.toString();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    public void testDefaultGenerationIfValid() throws Throwable {
        Generator<?> generator = generatorClass.newInstance();
        boolean valid = true;
        try {
            generator.init(context);
        } catch (InvalidGeneratorSetupException e) {
            // that's OK, not every Generator is available from default constructor
            valid = false;
        }
        if (valid) { // must be outside of catch block, else exceptions would be ignored
            generator.generate(new ProductWrapper());
        }
    }

    // helpers ---------------------------------------------------------------------------------------------------------
    
    protected void assertCustomToStringMethod(Generator<?> generator) {
        String s = generator.toString();
        String className = generator.getClass().getName();
        if (s.startsWith(className) && s.length() >= className.length() + 2
                && s.charAt(className.length()) == '@'
                && ParseUtil.isHex(s.substring(className.length() + 1)))
            throw new AssertionFailedError("The toString() method of class " + generator.getClass() +
                    " is not customized");
    }

}

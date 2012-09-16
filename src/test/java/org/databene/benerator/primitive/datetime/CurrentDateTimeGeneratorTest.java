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

package org.databene.benerator.primitive.datetime;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.databene.benerator.test.GeneratorTest;
import org.junit.Test;

/**
 * Tests the {@link CurrentDateGenerator}.<br/><br/>
 * Created: 15.04.2011 08:52:43
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class CurrentDateTimeGeneratorTest extends GeneratorTest {
	
	@Test
	public void test() {
		Date startDate = new Date();
		CurrentDateTimeGenerator generator = new CurrentDateTimeGenerator();
		generator.init(context);
		Date generatedDate = generator.generate();
		assertFalse(startDate.after(generatedDate));
		Calendar toleratedLimit = new GregorianCalendar();
		toleratedLimit.setTime(startDate);
		toleratedLimit.add(Calendar.SECOND, 2);
		assertTrue(generatedDate.before(toleratedLimit.getTime()));
	}
	
}

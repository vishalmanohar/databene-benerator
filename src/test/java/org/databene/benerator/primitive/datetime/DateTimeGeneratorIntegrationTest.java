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

package org.databene.benerator.primitive.datetime;

import static org.junit.Assert.*;

import org.databene.benerator.Generator;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.engine.parser.String2DistributionConverter;
import org.databene.benerator.engine.statement.BeanStatement;
import org.databene.benerator.test.BeneratorIntegrationTest;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.commons.TimeUtil;
import org.databene.commons.converter.ConverterManager;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the correct interaction of XML parser, 
 * Benerator engine and {@link DateTimeGenerator}.<br/><br/>
 * Created: 04.05.2010 06:13:08
 * @since 0.6.1
 * @author Volker Bergmann
 */
public class DateTimeGeneratorIntegrationTest extends BeneratorIntegrationTest {

	@Before
	public void setupConverterManager() {
		ConverterManager converterManager = ConverterManager.getInstance();
		converterManager.reset();
    	converterManager.registerConverterClass(String2DistributionConverter.class);
		converterManager.setContext(context);
	}

	@Test
	public void test() {
		// create DateTimeGenerator from XML descriptor
		String beanId = "datetime_gen";
		String xml =
			"<bean id='" + beanId + "' class='" + DateTimeGenerator.class.getName() + "'>" +
			"  <property name='minDate'          value='2008-09-01'/>" +
			"  <property name='maxDate'          value='2008-09-05'/>" +
			"  <property name='dateGranularity'    value='00-00-02'  />" +
			"  <property name='dateDistribution' value='step'      />" +
			"  <property name='minTime'          value='08:00:00'  />" +
			"  <property name='maxTime'          value='16:00:00'  />" +
			"  <property name='timeGranularity'    value='00:00:01'  />" +
			"  <property name='timeDistribution' value='step'      />" +
			"</bean>";
		BeanStatement statement = (BeanStatement) parse(xml);
		statement.execute(context);
		DateTimeGenerator generator = (DateTimeGenerator) GeneratorUtil.unwrap((Generator<?>) context.get(beanId));
		
		// check generator configuration
		assertEquals(TimeUtil.date(2008, 8, 1,  0, 0, 0, 0), generator.minDate);
		assertEquals(TimeUtil.date(2008, 8, 5,  0, 0, 0, 0), generator.maxDate);
		assertEquals(SequenceManager.STEP_SEQUENCE, generator.dateDistribution);
		assertEquals( 8 * 3600 * 1000, generator.minTime);
		assertEquals(16 * 3600 * 1000, generator.maxTime);
		assertEquals(1000, generator.timeGranularity);
		assertEquals(SequenceManager.STEP_SEQUENCE, generator.timeDistribution);
		
		// check generation
		assertEquals(TimeUtil.date(2008, 8, 1, 8, 0, 0, 0), generator.generate());
		assertEquals(TimeUtil.date(2008, 8, 3, 8, 0, 1, 0), generator.generate());
		assertEquals(TimeUtil.date(2008, 8, 5, 8, 0, 2, 0), generator.generate());
		assertNull(generator.generate());
	}
	
}

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

package org.databene.benerator.anno;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.databene.benerator.distribution.sequence.StepSequence;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.benerator.factory.EquivalenceGeneratorFactory;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.model.data.ArrayElementDescriptor;
import org.databene.model.data.ArrayTypeDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.platform.db.DBSystem;
import org.databene.platform.java.BeanDescriptorProvider;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link AnnotationMapper}.<br/><br/>
 * Created: 30.04.2010 13:57:59
 * @since 0.6.1
 * @author Volker Bergmann
 */
public class AnnotationMapperTest {

	private AnnotationMapper annotationMapper;
	private BeneratorContext context;

	@Before
	public void setUp() {
		DataModel dataModel = new DataModel();
		new BeanDescriptorProvider(dataModel);
		EquivalenceGeneratorFactory generatorFactory = new EquivalenceGeneratorFactory();
		context = new DefaultBeneratorContext();
		context.setDataModel(dataModel);
		context.setGeneratorFactory(generatorFactory);
		annotationMapper = new AnnotationMapper(generatorFactory, dataModel, new DefaultPathResolver());
	}

	@Test
	public void testUnannotated() throws Exception {
		checkMethod("unannotatedMethod", String.class, "string");
	}

	public void unannotatedMethod(String name) { }

	
	
	@Test
	public void testGenerator() throws Exception {
		checkMethod("generatorMethod", String.class, "string", "generator", "myGen");
	}

	public void generatorMethod(@Generator("myGen") String name) { }

	
	
	@Test
	public void testNullQuota() throws Exception {
		checkMethod("nullQuotaMethod", String.class, "string", "nullQuota", 1.);
	}

	public void nullQuotaMethod(@NullQuota(1) String name) { }

	
	
	@Test
	public void testUniqueMethod() throws Exception {
	    Method stringMethod = getClass().getDeclaredMethod("uniqueMethod", new Class[] { String.class });
	    BeneratorContext context = new DefaultBeneratorContext();
		AnnotationMapper mapper = new AnnotationMapper(new EquivalenceGeneratorFactory(), context.getDataModel(), new DefaultPathResolver());
		ArrayTypeDescriptor type = mapper.createMethodParamsType(stringMethod);
		InstanceDescriptor arrayDescriptor = mapper.createMethodParamsInstanceDescriptor(stringMethod, type);
		assertEquals(true, arrayDescriptor.isUnique());
	}
	
	@Unique
	public void uniqueMethod(String name) { }

	
	
	@Test
	public void testUniqueParam() throws Exception {
		checkMethod("uniqueParam", String.class, "string", "unique", true);
	}

	public void uniqueParam(@Unique String name) { }

	
	
	@Test
	public void testValues() throws Exception {
		checkMethod("valuesMethod", String.class, "string", "values", "'A','B'" );
	}

	public void valuesMethod(@Values({"A", "B"}) String name) { }

	
	
	@Test
	public void testPattern() throws Exception {
		checkMethod("patternMethod", String.class, "string", "pattern", "ABC");
	}

	public void patternMethod(@Pattern(regexp = "ABC") String name) { }

	
	
	@Test
	public void testPatternMinMaxLength() throws Exception {
		checkMethod("patternMinMaxLengthMethod", String.class, "string", "pattern", "[A-Z]*", "minLength", 5, "maxLength", 8);
	}

	public void patternMinMaxLengthMethod(@Pattern(regexp = "[A-Z]*") @Size(min = 5, max = 8) String name) { }
	

	
	@Test
	public void testDbSource() throws Exception {
		checkMethod("dbSourceMethod", String.class, "string", 
				"source", "db", 
				"selector", "select id from db_user");
	}

	public void dbSourceMethod(@Source(id = "db", selector ="select id from db_user") String name) { }
	

	
	@Test
	public void testFileSource() throws Exception {
		checkMethod("fileSourceMethod", String.class, "string", 
				"source", "customers.csv", 
				"dataset", "DE",
				"nesting", "region",
				"separator", ";", 
				"encoding", "UTF-8", 
				"filter", "candidate.age >= 18");
	}

	public void fileSourceMethod(
			@Source(uri = "customers.csv", dataset = "DE", nesting = "region", separator = ";", 
					encoding = "UTF-8", filter ="candidate.age >= 18")
			String name) { 
	}
	

	
	// test number generation settings ---------------------------------------------------------------------------------
	
	@Test
	public void testStdSequenceInt() throws Exception {
		checkMethod("predefSequenceIntMethod", int.class, "int", 
				"min", "3", 
				"max", "8", 
				"granularity", "2",
				"distribution", "cumulated");
	}

	public void predefSequenceIntMethod(@Min(3) @Max(8) @Granularity(2) @Distribution("cumulated") int n) { }
	

	
	@Test
	public void testSequenceClassInt() throws Exception {
		checkMethod("sequenceClassIntMethod", int.class, "int", 
				"distribution", StepSequence.class.getName());
	}

	public void sequenceClassIntMethod(
			@Distribution("org.databene.benerator.distribution.sequence.StepSequence") int n) { }
	

	
	@Test
	public void testSequenceCtorInt() throws Exception {
		checkMethod("sequenceCtorIntMethod", int.class, "int", 
				"distribution", "new " + StepSequence.class.getName() + "()");
	}

	public void sequenceCtorIntMethod(
			@Distribution("new org.databene.benerator.distribution.sequence.StepSequence()") int n) { }
	
	
	
	// testing lengths -------------------------------------------------------------------------------------------------
	
	@Test
	public void testPredefLengthSequenceInt() throws Exception {
		checkMethod("predefLengthSequenceIntMethod", String.class, "string", 
				"minLength", 3, 
				"maxLength", 8, 
				"lengthDistribution", "cumulated");
	}

	public void predefLengthSequenceIntMethod(
			@Size(min = 3, max = 8) @SizeDistribution("cumulated") String s) { }
	

	
	@Test
	public void testLengthSequenceClassInt() throws Exception {
		checkMethod("lengthSequenceClassIntMethod", String.class, "string", 
				"lengthDistribution", StepSequence.class.getName());
	}

	public void lengthSequenceClassIntMethod(
			@SizeDistribution("org.databene.benerator.distribution.sequence.StepSequence") String s) { }
	

	
	@Test
	public void testLengthSequenceCtorInt() throws Exception {
		checkMethod("lengthSequenceCtorIntMethod", String.class, "string", 
				"lengthDistribution", "new " + StepSequence.class.getName() + "()");
	}

	public void lengthSequenceCtorIntMethod(
			@SizeDistribution("new org.databene.benerator.distribution.sequence.StepSequence()") String s) { }
	
	
	
	// test class annotations ------------------------------------------------------------------------------------------
	
	@Test
	public void testDatabaseAnnotation() {
		annotationMapper.parseClassAnnotations(ClassWithDatabase.class.getAnnotations(), context);
		DBSystem db = (DBSystem) context.get("db");
		assertNotNull(db);
		assertEquals("hsqlmem", db.getEnvironment());
	}

	@Database(id = "db", environment = "hsqlmem")
	static class ClassWithDatabase {
	}
	
	@Test
	public void testSimpleBeanAnnotation() {
		annotationMapper.parseClassAnnotations(ClassWithSimpleBean.class.getAnnotations(), context);
		Object bean = context.get("bean");
		assertNotNull(bean);
		assertEquals(ArrayList.class, bean.getClass());
	}
	
	@Bean(id = "bean", type = ArrayList.class)
	static class ClassWithSimpleBean {
	}
	
	@Test
	public void testBeanSpecAnnotation() {
		annotationMapper.parseClassAnnotations(ClassWithBeanSpec.class.getAnnotations(), context);
		Object bean = context.get("bean");
		assertNotNull(bean);
		assertEquals(Date.class, bean.getClass());
		assertEquals(123, ((Date) bean).getTime());
	}
	
	@Bean(id = "bean", spec = "new java.util.Date(123)")
	static class ClassWithBeanSpec {
	}
	
	@Test
	public void testBeanPropertiesAnnotation() {
		annotationMapper.parseClassAnnotations(ClassWithBeanProperties.class.getAnnotations(), context);
		Object bean = context.get("bean");
		assertNotNull(bean);
		assertEquals(Date.class, bean.getClass());
		assertEquals(234, ((Date) bean).getTime());
	}
	
	@Bean(id = "bean", type = Date.class, properties = { @Property(name="time", value="234") })
	static class ClassWithBeanProperties {
	}
	
	@Test
	public void testBeanPropertiesSpecAnnotation() {
		annotationMapper.parseClassAnnotations(ClassWithBeanPropertiesSpec.class.getAnnotations(), context);
		Object bean = context.get("bean");
		assertNotNull(bean);
		assertEquals(Date.class, bean.getClass());
		assertEquals(345, ((Date) bean).getTime());
	}
	
	@Bean(id = "bean", spec = "new java.util.Date{ time = 345 }")
	static class ClassWithBeanPropertiesSpec {
	}
	
	@Test
	public void testBeanSource() {
		annotationMapper.parseClassAnnotations(ClassWithBeanSource.class.getAnnotations(), context);
		Object bean = context.get("bean");
		assertNotNull(bean);
		assertEquals(ConstantGenerator.class, bean.getClass());
		assertEquals(42, ((ConstantGenerator<?>) bean).getValue());
	}
	
	@Bean(id = "bean", spec = "new org.databene.benerator.sample.ConstantGenerator(42)")
	static class ClassWithBeanSource {
		@Test
		@Source("bean")
		public void test(int value) {
			
		}
	}
	
	
	// helper methods --------------------------------------------------------------------------------------------------
	
	private void checkMethod(String methodName, Class<?> methodArgType, String expectedType, Object ... details)
            throws NoSuchMethodException {
	    Method stringMethod = getClass().getDeclaredMethod(methodName, new Class[] { methodArgType });
		DefaultBeneratorContext context = new DefaultBeneratorContext();
	    AnnotationMapper mapper = new AnnotationMapper(new EquivalenceGeneratorFactory(), context.getDataModel(), new DefaultPathResolver());
		ArrayTypeDescriptor type = mapper.createMethodParamsType(stringMethod);
		InstanceDescriptor arrayDescriptor = mapper.createMethodParamsInstanceDescriptor(stringMethod, type);
		ArrayTypeDescriptor typeDescriptor = (ArrayTypeDescriptor) arrayDescriptor.getTypeDescriptor();
		ArrayTypeDescriptor parentTypeDescriptor = typeDescriptor.getParent();
		assertEquals(1, parentTypeDescriptor.getElements().size());
		ArrayElementDescriptor param1 = typeDescriptor.getElement(0);
		assertEquals(expectedType, ((SimpleTypeDescriptor) parentTypeDescriptor.getElement(0).getTypeDescriptor()).getPrimitiveType().getName());
		for (int i = 0; i < details.length; i += 2) {
	        String detailName = (String) details[i];
	        Object expectedValue = details[i + 1];
			Object actualValue;
			if (param1.supportsDetail(detailName))
				actualValue = param1.getDetailValue(detailName);
			else
				actualValue = param1.getTypeDescriptor().getDetailValue(detailName);
			assertEquals(expectedValue, actualValue);
        }
    }

}

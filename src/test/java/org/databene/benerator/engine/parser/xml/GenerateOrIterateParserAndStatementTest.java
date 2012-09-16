/*
 * (c) Copyright 2009-2012 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine.parser.xml;

import static org.junit.Assert.*;

import java.util.List;

import org.databene.benerator.Generator;
import org.databene.benerator.SequenceTestGenerator;
import org.databene.benerator.engine.BeneratorMonitor;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.primitive.IncrementGenerator;
import org.databene.benerator.test.BeneratorIntegrationTest;
import org.databene.benerator.test.ConsumerMock;
import org.databene.benerator.test.PersonSource;
import org.databene.commons.CollectionUtil;
import org.databene.commons.converter.UnsafeConverter;
import org.databene.commons.validator.AbstractValidator;
import org.databene.jdbacl.dialect.HSQLUtil;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.EntitySource;
import org.databene.platform.db.DBSystem;
import org.databene.webdecs.DataIterator;
import org.databene.webdecs.DataSource;
import org.databene.webdecs.util.DataIteratorTestCase;
import org.junit.Test;

/**
 * Tests the {@link GenerateOrIterateParser}.<br/><br/>
 * Created: 10.11.2009 15:08:46
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class GenerateOrIterateParserAndStatementTest extends BeneratorIntegrationTest {

	@Test
	public void testPaging() throws Exception {
		BeneratorMonitor.INSTANCE.setTotalGenerationCount(0);
		Statement statement = parse(
				"<generate type='dummy' count='{c}' pageSize='{ps}' consumer='cons'/>");
		ConsumerMock consumer = new ConsumerMock(false);
		context.set("cons", consumer);
		context.set("c", 100);
		context.set("ps", 20);
		statement.execute(context);
		assertEquals(100, consumer.startConsumingCount.get());
		assertEquals(100, consumer.finishConsumingCount.get());
		assertEquals(100L, BeneratorMonitor.INSTANCE.getTotalGenerationCount());
	}

	@Test
	public void testConverter() throws Exception {
		BeneratorMonitor.INSTANCE.setTotalGenerationCount(0);
		Statement statement = parse("<generate type='dummy' count='3' converter='conv' consumer='cons'/>");
		ConsumerMock consumer = new ConsumerMock(true);
		context.set("cons", consumer);
		context.set("conv", new UnsafeConverter<Entity,Entity>(Entity.class, Entity.class) {
			public Entity convert(Entity sourceValue) {
				ComplexTypeDescriptor descriptor = sourceValue.descriptor();
				descriptor.setName("CONV_DUMMY");
				Entity result = new Entity(descriptor);
				return result;
			}
		});
		statement.execute(context);
		List<?> products = consumer.getProducts();
		assertEquals(3, products.size());
		for (int i = 0; i < 3; i++)
			assertEquals("CONV_DUMMY", ((Entity) products.get(i)).type());
		assertEquals(3L, BeneratorMonitor.INSTANCE.getTotalGenerationCount());
	}

	@Test
	public void testValidator() throws Exception {
		BeneratorMonitor.INSTANCE.setTotalGenerationCount(0);
		Statement statement = parse(
				"<generate type='dummy' count='3' validator='vali' consumer='cons'>" +
				"	<id name='id' type='int' />" +
				"</generate>");
		ConsumerMock consumer = new ConsumerMock(true);
		context.set("cons", consumer);
		context.set("vali", new AbstractValidator<Entity>() {
			public boolean valid(Entity entity) {
				return ((Integer) entity.get("id")) % 2 == 0;
			}
		});
		statement.execute(context);
		List<?> products = consumer.getProducts();
		assertEquals(3, products.size());
		for (int i = 0; i < 3; i++)
			assertEquals(2 + 2 * i, ((Entity) products.get(i)).get("id"));
		assertEquals(3L, BeneratorMonitor.INSTANCE.getTotalGenerationCount());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testArray() throws Exception {
		Statement statement = parse(
				"<generate type='array' count='5' consumer='cons'>" +
				"  <value pattern='ABC' />" +
				"  <value type='int' constant='42' />" +
				"</generate>");
		ConsumerMock consumer = new ConsumerMock(true);
		context.set("cons", consumer);
		statement.execute(context);
		List<Object[]> products = (List) consumer.getProducts();
		assertEquals(5, products.size());
		Object[] array1 = products.get(0);
		assertEquals(2, array1.length);
		assertEquals("ABC", array1[0]);
		assertEquals(42, array1[1]);
		Object[] array2 = products.get(1);
		assertEquals(2, array2.length);
		assertEquals("ABC", array2[0]);
		assertEquals(42, array2[1]);
	}

    @Test
	public void testGeneratePageSize2() throws Exception {
		BeneratorMonitor.INSTANCE.setTotalGenerationCount(0);
		ConsumerMock cons = new ConsumerMock(false);
		context.set("cons", cons);
		Statement statement = parse(
			"<generate type='top' count='4' pageSize='2' consumer='cons' />"
        );
		statement.execute(context);
		context.close();
		List<String> expectedInvocations = CollectionUtil.toList(
				ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
				ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
				ConsumerMock.FLUSH,
				ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
				ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
				ConsumerMock.FLUSH
		);
		assertEquals(expectedInvocations, cons.invocations);
		assertEquals(4L, BeneratorMonitor.INSTANCE.getTotalGenerationCount());
	}

    @Test
	public void testGeneratePageSize0() throws Exception {
		BeneratorMonitor.INSTANCE.setTotalGenerationCount(0);
		ConsumerMock cons = new ConsumerMock(false);
		context.set("cons", cons);
		Statement statement = parse(
			"<generate type='top' count='4' pageSize='0' consumer='cons' />"
        );
		statement.execute(context);
		List<String> expectedInvocations = CollectionUtil.toList(
				ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
				ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
				ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
				ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING
		);
		assertEquals(expectedInvocations, cons.invocations);
		assertEquals(4L, BeneratorMonitor.INSTANCE.getTotalGenerationCount());
	}

    @Test
	public void testSimpleSubGenerate() throws Exception {
		BeneratorMonitor.INSTANCE.setTotalGenerationCount(0);
		Statement statement = parse(
				"<generate type='top' count='3' consumer='cons1'>" +
        		"    <generate type='sub' count='2' consumer='new " + ConsumerMock.class.getName() + "(false, 2)'/>" +
        		"</generate>"
        );
		ConsumerMock outerConsumer = new ConsumerMock(false, 1);
		context.set("cons1", outerConsumer);
		statement.execute(context);
		assertEquals(3, outerConsumer.startConsumingCount.get());
		assertTrue(outerConsumer.closeCount.get() == 0);
		ConsumerMock innerConsumer = ConsumerMock.instances.get(2);
		assertEquals(6, innerConsumer.startConsumingCount.get());
		assertTrue(innerConsumer.flushCount.get() > 0);
		assertTrue(outerConsumer.closeCount.get() == 0);
		assertTrue(innerConsumer.closeCount.get() > 0);
		assertEquals(9L, BeneratorMonitor.INSTANCE.getTotalGenerationCount());
	}

    @Test
	public void testSubGenerateLifeCycle() throws Exception {
		BeneratorMonitor.INSTANCE.setTotalGenerationCount(0);
		Statement statement = parse(
				"<generate type='top' count='3'>" +
        		"    <generate type='sub' count='2' consumer='new " + ConsumerMock.class.getName() + "(true)'>" +
        		"		<attribute name='x' type='int' generator='" + IncrementGenerator.class.getName() + "' />" +
				"	</generate>" +
        		"</generate>"
        );
		statement.execute(context);
		ConsumerMock innerConsumer = ConsumerMock.instances.get(0);
		assertEquals(6, innerConsumer.products.size());
		assertEquals(1, ((Entity) innerConsumer.products.get(0)).get("x"));
		assertEquals(2, ((Entity) innerConsumer.products.get(1)).get("x"));
		assertEquals(1, ((Entity) innerConsumer.products.get(2)).get("x"));
		assertEquals(2, ((Entity) innerConsumer.products.get(3)).get("x"));
		assertEquals(1, ((Entity) innerConsumer.products.get(4)).get("x"));
		assertEquals(2, ((Entity) innerConsumer.products.get(5)).get("x"));
		assertTrue(innerConsumer.flushCount.get() > 0);
		assertTrue(innerConsumer.closeCount.get() > 0);
		assertEquals(9L, BeneratorMonitor.INSTANCE.getTotalGenerationCount());
	}

    @Test
	public void testSubGeneratePageSize2() throws Exception {
		BeneratorMonitor.INSTANCE.setTotalGenerationCount(0);
		ConsumerMock cons = new ConsumerMock(false);
		context.set("cons", cons);
		Statement statement = parse(
				"<generate type='top' count='2' pageSize='1' consumer='cons'>" +
        		"    <generate type='sub' count='4' pageSize='2' consumer='cons'/>" +
        		"</generate>"
        );
		statement.execute(context);
		List<String> expectedInvocations = CollectionUtil.toList(
				ConsumerMock.START_CONSUMING,
					ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
					ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
					ConsumerMock.FLUSH,
					ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
					ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
					ConsumerMock.FLUSH,
				ConsumerMock.FINISH_CONSUMING,
				ConsumerMock.FLUSH,

				ConsumerMock.START_CONSUMING,
					ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
					ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
					ConsumerMock.FLUSH,
					ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
					ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
					ConsumerMock.FLUSH,
				ConsumerMock.FINISH_CONSUMING,
				ConsumerMock.FLUSH
		);
		assertEquals(expectedInvocations, cons.invocations);
		assertEquals(10L, BeneratorMonitor.INSTANCE.getTotalGenerationCount());
	}

    @Test
	public void testSubGeneratePageSize0() throws Exception {
		BeneratorMonitor.INSTANCE.setTotalGenerationCount(0);
		ConsumerMock cons = new ConsumerMock(false);
		context.set("cons", cons);
		Statement statement = parse(
				"<generate type='top' count='2' pageSize='1' consumer='cons'>" +
        		"    <generate type='sub' count='1' pageSize='0' consumer='cons'/>" +
        		"</generate>"
        );
		statement.execute(context);
		List<String> expectedInvocations = CollectionUtil.toList(
				ConsumerMock.START_CONSUMING,
				ConsumerMock.START_CONSUMING,
				ConsumerMock.FINISH_CONSUMING,
				ConsumerMock.FINISH_CONSUMING,
				ConsumerMock.FLUSH,
				ConsumerMock.START_CONSUMING,
				ConsumerMock.START_CONSUMING,
				ConsumerMock.FINISH_CONSUMING,
				ConsumerMock.FINISH_CONSUMING,
				ConsumerMock.FLUSH
		);
		assertEquals(expectedInvocations, cons.invocations);
		assertEquals(4L, BeneratorMonitor.INSTANCE.getTotalGenerationCount());
	}

    /** Tests a sub loop that derives its loop length from a parent attribute. */
	@Test
	public void testSubGenerateParentRef() throws Exception {
		Statement statement = parse(
				"<generate name='pName' type='outer' count='3' consumer='cons'>" +
				"    <attribute name='n' type='int' distribution='step' />" +
				"    <generate type='inner' count='pName.n' consumer='cons'/>" + 
        		"</generate>");
		ConsumerMock consumer = new ConsumerMock(true, 1);
		context.set("cons", consumer);
		statement.execute(context);
		assertEquals(9, consumer.startConsumingCount.get());
		assertOuter(1, consumer.products.get(0));
		assertEquals(createEntity("inner"), consumer.products.get(1));
		assertOuter(2, consumer.products.get(2));
		assertEquals(createEntity("inner"), consumer.products.get(3));
		assertEquals(createEntity("inner"), consumer.products.get(4));
		assertOuter(3, consumer.products.get(5));
		assertEquals(createEntity("inner"), consumer.products.get(6));
		assertEquals(createEntity("inner"), consumer.products.get(7));
		assertEquals(createEntity("inner"), consumer.products.get(8));
	}

    /** Tests a combination of variable and attribute with the same name. */
	@Test
	public void testVariableOfSameNameAsAttribute() throws Exception {
		Statement statement = parse(
				"<generate name='pName' type='outer' count='3' consumer='cons'>" +
				"    <variable name='n' type='int' distribution='step' />" +
				"    <attribute name='n' type='int' script='n + 1' />" +
        		"</generate>");
		ConsumerMock consumer = new ConsumerMock(true, 1);
		context.set("cons", consumer);
		statement.execute(context);
		assertEquals(3, consumer.startConsumingCount.get());
		assertOuter(2, consumer.products.get(0));
		assertOuter(3, consumer.products.get(1));
		assertOuter(4, consumer.products.get(2));
	}

	private void assertOuter(int n, Object object) {
		Entity entity = (Entity) object;
	    assertNotNull(entity);
	    assertEquals("outer", entity.type());
	    assertEquals(n, ((Integer) entity.get("n")).intValue());
    }

	/** Tests the nesting of an &lt;execute&gt; element within a &lt;generate&gt; element */
	@Test
	public void testSubExecute() throws Exception {
		Statement statement = parse(
				"<generate type='dummy' count='3'>" +
        		"	<execute>bean.invoke(2)</execute>" +
        		"</generate>");
		BeanMock bean = new BeanMock();
		bean.invocationCount = 0;
		context.set("bean", bean);
		statement.execute(context);
		assertEquals(3, bean.invocationCount);
		assertEquals(2, bean.lastValue);
	}
	
	/** Tests iterating an {@link EntitySource} */
	@Test
	public void testIterate() throws Exception {
		Statement statement = parse("<iterate type='Person' source='personSource' consumer='cons' />");
		PersonSource source = new PersonSource();
		source.setContext(context);
		context.set("personSource", source);
		ConsumerMock consumer = new ConsumerMock(true);
		context.set("cons", consumer);
		statement.execute(context);
		assertEquals(2, consumer.products.size());
		assertEquals(source.createPersons(), consumer.products);
	}
	
	/** Tests pure {@link Entity} generation */
	@Test
	public void testGenerate() throws Exception {
		Statement statement = parse("<generate type='Person' count='2' consumer='cons' />");
		ConsumerMock consumer = new ConsumerMock(false);
		context.set("cons", consumer);
		statement.execute(context);
		assertEquals(2, consumer.startConsumingCount.get());
		assertEquals(2, consumer.finishConsumingCount.get());
	}
	
	/** Tests DB update */
	@Test
	public void testDBUpdate() throws Exception {
		// create DB
        DBSystem db = new DBSystem("db", HSQLUtil.getInMemoryURL("benetest"), 
        		HSQLUtil.DRIVER, HSQLUtil.DEFAULT_USER, HSQLUtil.DEFAULT_PASSWORD, context.getDataModel());
        try {
    		// prepare DB
        	db.execute(
        		"create table GOIPAST (" +
        		"	ID int," +
        		"	N  int," +
        		"	primary key (ID)" +
        		")");
        	db.execute("insert into GOIPAST (id, n) values (1, 3)");
        	db.execute("insert into GOIPAST (id, n) values (2, 4)");
	        // parse and run statement
	        Statement statement = parse(
	        	"<iterate type='GOIPAST' source='db' consumer='db.updater()'>" +
	        	"	<attribute name='n' constant='2' />" +
	        	"</iterate>"
	        );
	        context.set("db", db);
			statement.execute(context);
			DataSource<?> check = db.query("select N from GOIPAST", true, context);
			DataIterator<?> iterator = check.iterator();
			DataIteratorTestCase.expectNextElements(iterator, 2, 2).withNoNext();
			iterator.close();
        } catch (Exception e) {
        	e.printStackTrace();
        	throw e;
        } finally {
        	// clean up
        	db.execute("drop table GOIPAST");
        	db.close();
        }
	}

	@Test
	public void testGenerateWithOffset() throws Exception {
		Statement statement = parse(
				"<generate name='array' count='3' consumer='cons'>" +
				"    <value type='int' distribution='step' offset='2'/>" +
        		"</generate>");
		ConsumerMock consumer = new ConsumerMock(true, 1);
		context.set("cons", consumer);
		statement.execute(context);
		assertEquals(3, consumer.startConsumingCount.get());
		assertArrayEquals(new Object[] { 3 }, (Object[]) consumer.products.get(0));
		assertArrayEquals(new Object[] { 4 }, (Object[]) consumer.products.get(1));
		assertArrayEquals(new Object[] { 5 }, (Object[]) consumer.products.get(2));
	}
	
	@Test
	public void testIterateWithOffset() throws Exception {
		Generator<Integer[]> source = new SequenceTestGenerator<Integer[]>(
				new Integer[] { 1 }, 
				new Integer[] { 2 }, 
				new Integer[] { 3 }, 
				new Integer[] { 4 }, 
				new Integer[] { 5 });
		context.set("source", source);
		Statement statement = parse("<iterate source='source' offset='2' type='array' count='3' consumer='cons' />");
		ConsumerMock consumer = new ConsumerMock(true, 1);
		context.set("cons", consumer);
		statement.execute(context);
		assertEquals(3, consumer.startConsumingCount.get());
		assertArrayEquals(new Object[] { 3 }, (Object[]) consumer.products.get(0));
		assertArrayEquals(new Object[] { 4 }, (Object[]) consumer.products.get(1));
		assertArrayEquals(new Object[] { 5 }, (Object[]) consumer.products.get(2));
	}
	
	@Test
	public void testScopeWithAttributes() {
		Statement statement = parse(
				"<generate name='a' count='2' consumer='NoConsumer'>" +
				"	<generate name='b' count='2' consumer='NoConsumer'>" +
				"		<generate name='c' count='2' consumer='ConsoleExporter,cons'>" +
				"			<attribute name='slash' type='int' distribution='increment' scope='/'/>" +
				"			<attribute name='a' type='int' distribution='increment' scope='a'/>" +
				"			<attribute name='b' type='int' distribution='increment' scope='b'/>" +
				"			<attribute name='c' type='int' distribution='increment' scope='c'/>" +
				"			<attribute name='def' type='int' distribution='increment'/>" +
        		"		</generate>" + 
        		"	</generate>" + 
        		"</generate>"
				);
		ConsumerMock consumer = new ConsumerMock(true, 1);
		context.set("cons", consumer);
		statement.execute(context);
		assertEquals(8, consumer.startConsumingCount.get());
		assertComponents((Entity) consumer.products.get(0), "slash", 1, "a", 1, "b", 1, "c", 1, "def", 1);
		assertComponents((Entity) consumer.products.get(1), "slash", 2, "a", 2, "b", 2, "c", 2, "def", 2);
		assertComponents((Entity) consumer.products.get(2), "slash", 3, "a", 3, "b", 3, "c", 1, "def", 1);
		assertComponents((Entity) consumer.products.get(3), "slash", 4, "a", 4, "b", 4, "c", 2, "def", 2);
		assertComponents((Entity) consumer.products.get(4), "slash", 5, "a", 5, "b", 1, "c", 1, "def", 1);
		assertComponents((Entity) consumer.products.get(5), "slash", 6, "a", 6, "b", 2, "c", 2, "def", 2);
		assertComponents((Entity) consumer.products.get(6), "slash", 7, "a", 7, "b", 3, "c", 1, "def", 1);
		assertComponents((Entity) consumer.products.get(7), "slash", 8, "a", 8, "b", 4, "c", 2, "def", 2);
	}

	@Test
	public void testScopeWithVariables() {
		Statement statement = parse(
				"<generate name='a' count='2' consumer='NoConsumer'>" +
				"	<generate name='b' count='2' consumer='NoConsumer'>" +
				"		<generate name='c' count='2' consumer='ConsoleExporter,cons'>" +
				"			<variable name='slash'  type='int' distribution='increment' scope='/'/>" +
				"			<variable name='a'      type='int' distribution='increment' scope='a'/>" +
				"			<variable name='b'      type='int' distribution='increment' scope='b'/>" +
				"			<variable name='c'      type='int' distribution='increment' scope='c'/>" +
				"			<variable name='def'    type='int' distribution='increment'/>" +

				"			<attribute name='slash' type='int' script='slash'/>" +
				"			<attribute name='a'     type='int' script='a'/>" +
				"			<attribute name='b'     type='int' script='b'/>" +
				"			<attribute name='c'     type='int' script='c'/>" +
				"			<attribute name='def'   type='int' script='def'/>" +
        		"		</generate>" + 
        		"	</generate>" + 
        		"</generate>"
				);
		ConsumerMock consumer = new ConsumerMock(true, 1);
		context.set("cons", consumer);
		statement.execute(context);
		assertEquals(8, consumer.startConsumingCount.get());
		assertComponents((Entity) consumer.products.get(0), "slash", 1, "a", 1, "b", 1, "c", 1, "def", 1);
		assertComponents((Entity) consumer.products.get(1), "slash", 2, "a", 2, "b", 2, "c", 2, "def", 2);
		assertComponents((Entity) consumer.products.get(2), "slash", 3, "a", 3, "b", 3, "c", 1, "def", 1);
		assertComponents((Entity) consumer.products.get(3), "slash", 4, "a", 4, "b", 4, "c", 2, "def", 2);
		assertComponents((Entity) consumer.products.get(4), "slash", 5, "a", 5, "b", 1, "c", 1, "def", 1);
		assertComponents((Entity) consumer.products.get(5), "slash", 6, "a", 6, "b", 2, "c", 2, "def", 2);
		assertComponents((Entity) consumer.products.get(6), "slash", 7, "a", 7, "b", 3, "c", 1, "def", 1);
		assertComponents((Entity) consumer.products.get(7), "slash", 8, "a", 8, "b", 4, "c", 2, "def", 2);
	}

}

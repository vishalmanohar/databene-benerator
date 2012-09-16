/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.xls;

import java.util.Date;
import java.util.List;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.commons.converter.NoOpConverter;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.DefaultDescriptorProvider;
import org.databene.model.data.DescriptorProvider;
import org.databene.model.data.Entity;
import org.databene.model.data.PartDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.webdecs.DataContainer;
import org.databene.webdecs.DataIterator;
import org.databene.webdecs.DataUtil;

import org.junit.Before;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link XLSEntityIterator} class.<br/>
 * <br/>
 * Created at 29.01.2009 11:06:33
 * @since 0.5.8
 * @author Volker Bergmann
 */

public class XLSEntityIteratorTest extends XLSTest {
	
	private static final String PRODUCT_XLS = "org/databene/platform/xls/product-singlesheet.ent.xls";
	private static final String IMPORT_XLS = "org/databene/platform/xls/import-multisheet.ent.xls";
	private static final String PRODUCT_COLUMNS_XLS = "org/databene/platform/xls/product-columns.ent.xls";
	
	BeneratorContext context;

    @Before
	public void setUp() {
		context = new DefaultBeneratorContext();
	}
	
	@Test
	public void testImport() throws Exception {
		XLSEntityIterator iterator = new XLSEntityIterator(IMPORT_XLS);
		iterator.setContext(context);
		try {
			assertProduct(PROD1, DataUtil.nextNotNullData(iterator));
			Entity next = DataUtil.nextNotNullData(iterator);
			assertProduct(PROD2, next);
			assertPerson(PERSON1, DataUtil.nextNotNullData(iterator));
			assertNull(iterator.next(new DataContainer<Entity>()));
		} finally {
			iterator.close();
		}
	}
	
	@Test
	public void testImportPredefinedEntityType() throws Exception {
		XLSEntityIterator iterator = new XLSEntityIterator(IMPORT_XLS, new NoOpConverter<String>(), new ComplexTypeDescriptor("XYZ", context.getLocalDescriptorProvider()));
		iterator.setContext(context);
		try {
			assertXYZ(XYZ11, DataUtil.nextNotNullData(iterator));
			Entity next = DataUtil.nextNotNullData(iterator);
			assertXYZ(XYZ12, next);
			Entity entity = DataUtil.nextNotNullData(iterator);
			assertEquals("XYZ", entity.type());
			assertNull(iterator.next(new DataContainer<Entity>()));
			assertEquals("Alice", entity.get("name"));
		} finally {
			iterator.close();
		}
	}
	
	@Test
	public void testParseAll() throws Exception {
		List<Entity> entities = XLSEntityIterator.parseAll(IMPORT_XLS, null);
		assertEquals(3, entities.size());
		assertProduct(PROD1, entities.get(0));
		assertProduct(PROD2, entities.get(1));
		assertPerson(PERSON1, entities.get(2));
	}

	@Test
	public void testTypes() throws Exception {
		DescriptorProvider dp = new DefaultDescriptorProvider("test", new DataModel());
		// Create descriptor
		final ComplexTypeDescriptor descriptor = new ComplexTypeDescriptor("Product", dp);
		descriptor.addComponent(new PartDescriptor("ean", dp, "string"));
		SimpleTypeDescriptor priceTypeDescriptor = new SimpleTypeDescriptor("priceType", dp, "big_decimal");
		priceTypeDescriptor.setGranularity("0.01");
		descriptor.addComponent(new PartDescriptor("price", dp, priceTypeDescriptor));
		descriptor.addComponent(new PartDescriptor("date", dp, "date"));
		descriptor.addComponent(new PartDescriptor("available", dp, "boolean"));
		descriptor.addComponent(new PartDescriptor("updated", dp, "timestamp"));
		context.getDataModel().addDescriptorProvider(dp);
		
		// test import
		XLSEntityIterator iterator = new XLSEntityIterator(PRODUCT_XLS);
		iterator.setContext(context);
		try {
			assertProduct(PROD1, DataUtil.nextNotNullData(iterator));
			assertProduct(PROD2, DataUtil.nextNotNullData(iterator));
			assertNull(iterator.next(new DataContainer<Entity>()));
		} finally {
			iterator.close();
			context.getDataModel().removeDescriptorProvider("test");
		}
	}
	
	@Test
	public void testColumnIteration() throws Exception {
		// test import
		XLSEntityIterator iterator = new XLSEntityIterator(PRODUCT_COLUMNS_XLS);
		iterator.setContext(context);
		iterator.setRowBased(false);
		try {
			assertProduct(PROD1, DataUtil.nextNotNullData(iterator));
			assertProduct(PROD2, DataUtil.nextNotNullData(iterator));
			assertNull(iterator.next(new DataContainer<Entity>()));
		} finally {
			iterator.close();
		}
	}
	
	// private helpers -------------------------------------------------------------------------------------------------
	
	private void assertXYZ(Entity expected, Entity actual) {
		assertEquals("XYZ", actual.type());
		assertEquals(expected.getComponent("ean"), actual.getComponent("ean"));
		assertEquals(((Number) expected.getComponent("price")).doubleValue(), ((Number) actual.getComponent("price")).doubleValue(), 0.000001);
		assertEquals(expected.getComponent("date"), actual.getComponent("date"));
		assertEquals(expected.getComponent("avail"), actual.getComponent("avail"));
		assertEquals(((Date) expected.getComponent("updated")).getTime(), 
				((Date) actual.getComponent("updated")).getTime());
    }

	private void assertProduct(Entity expected, Entity actual) {
		assertEquals("Product", actual.type());
		assertEquals(expected.getComponent("ean"), actual.getComponent("ean"));
		assertEquals(((Number) expected.getComponent("price")).doubleValue(), ((Number) actual.getComponent("price")).doubleValue(), 0.000001);
		assertEquals(expected.getComponent("date"), actual.getComponent("date"));
		assertEquals(expected.getComponent("avail"), actual.getComponent("avail"));
		assertEquals(((Date) expected.getComponent("updated")).getTime(), 
				((Date) actual.getComponent("updated")).getTime());
    }

    private void assertPerson(Entity expected, Entity actual) {
		assertEquals("Person", actual.type());
		assertEquals(expected.get("name"), actual.get("name"));
		assertEquals(expected.get("age"), ((Number) actual.get("age")).intValue());
    }

	public static void assertUnavailable(DataIterator<Entity> iterator) {
		assertNull(iterator.next(new DataContainer<Entity>()));
	}
    
}

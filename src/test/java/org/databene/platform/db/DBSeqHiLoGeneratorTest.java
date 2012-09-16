/*
 * (c) Copyright 2008-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.db;

import org.databene.benerator.test.GeneratorTest;
import org.databene.jdbacl.dialect.HSQLUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.*;
import static org.databene.jdbacl.dialect.HSQLUtil.*;

/**
 * Tests the DBSeqHiLoGenerator.<br/><br/>
 * Created: 27.01.2008 10:47:39
 * @since 0.4.0
 * @author Volker Bergmann
 */
public class DBSeqHiLoGeneratorTest extends GeneratorTest {

    private static final String SEQUENCE_NAME = "seq_id_gen";
    private static Logger logger = LoggerFactory.getLogger(DBSeqHiLoGeneratorTest.class);

    private DBSystem db;
    
    @Before
    public void setUpDatabase() throws Exception {
    	String url = HSQLUtil.getInMemoryURL("beneratortest");
    	db = new DBSystem("db", url, DRIVER, DEFAULT_USER, DEFAULT_PASSWORD, context.getDataModel());
    	dropSequence();
    	db.createSequence(SEQUENCE_NAME);
    }

    @After
    public void tearDown() {
    	dropSequence();
    }
    
    // test methods ----------------------------------------------------------------------------------------------------
    
    @Test
    public void testMaxLo2() throws Exception {
    	DBSeqHiLoGenerator generator = new DBSeqHiLoGenerator(SEQUENCE_NAME, 2, db);
    	generator.init(context);
        expectSequence(generator, 3, 4, 5, 6);
        generator.close();
    }
    
    @Test
    public void testMaxLo100() throws Exception {
    	DBSeqHiLoGenerator generator = new DBSeqHiLoGenerator(SEQUENCE_NAME, 100, db);
    	generator.init(context);
        expectSequence(generator, 101, 102, 103, 104);
        generator.close();
    }
    
    // private helpers -------------------------------------------------------------------------------------------------

	private void dropSequence() {
	    try {
    		db.dropSequence(SEQUENCE_NAME);
    	} catch (Exception e) {
    		logger.error(e.getMessage(), e);
    	}
    }
    
    private void expectSequence(DBSeqHiLoGenerator generator, long ... values) {
        for (long expectedValue : values) {
            Long product = generator.generate();
            assertNotNull("Generator is not available: " + generator, product);
			assertEquals(expectedValue, product.longValue());
        }
    }

}

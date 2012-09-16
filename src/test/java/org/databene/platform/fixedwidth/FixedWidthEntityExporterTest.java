/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.fixedwidth;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.databene.benerator.test.ModelTest;
import org.databene.commons.ArrayFormat;
import org.databene.commons.Encodings;
import org.databene.commons.FileUtil;
import org.databene.commons.IOUtil;
import org.databene.model.data.Entity;
import org.databene.platform.fixedwidth.FixedWidthEntityExporter;
import org.junit.Test;

/**
 * Tests the {@link FixedWidthEntityExporter}.<br/><br/>
 * Created: 14.11.2009 10:04:46
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class FixedWidthEntityExporterTest extends ModelTest {

	private static final String ENCODING = Encodings.UTF_8;

	@Test
	public void testFormat() throws Exception {
		File file = tempFile();
		String uri = file.getAbsolutePath();
		FixedWidthEntityExporter exporter = new FixedWidthEntityExporter(uri, ENCODING, "left[10],right[10.2r0]");
		try {
			consumeEntity(exporter, 12, 34);       // int
			consumeEntity(exporter, 56, 9876543L); // long
			consumeEntity(exporter, 78, 9876543.); // round double
			consumeEntity(exporter, 90, 1.5);      // double
		} finally {
			exporter.close();
		}
		assertTrue(file.exists());
		String[] actualLines = IOUtil.readTextLines(file.getAbsolutePath(), true);
		String[] expectedLines = new String [] {
				"12        0000034.00",
				"56        9876543.00",
				"78        9876543.00",
				"90        0000001.50"
		};
		String message = "Expected [" + ArrayFormat.format(expectedLines) + "], but found [" + ArrayFormat.format(actualLines) + "]";
		assertTrue(message, Arrays.equals(expectedLines, actualLines));
		FileUtil.deleteIfExists(file);
	}

	private void consumeEntity(FixedWidthEntityExporter exporter, Number left, Number right) {
		Entity entity = createEntity("row", "left", left, "right", right);
		exporter.startProductConsumption(entity);
		exporter.finishProductConsumption(entity);
	}

	private File tempFile() throws IOException {
		File file = File.createTempFile(getClass().getSimpleName(), ".fcw", new File("target"));
		FileUtil.deleteIfExists(file);
		return file;
	}
	
}

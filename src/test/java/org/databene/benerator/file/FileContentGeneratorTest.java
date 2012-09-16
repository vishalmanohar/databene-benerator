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

package org.databene.benerator.file;

import java.io.File;
import java.io.IOException;

import org.databene.benerator.test.GeneratorTest;
import org.databene.commons.FileUtil;
import org.databene.commons.IOUtil;

/**
 * Parent class for tests that relate to file and folder hierarchies.<br/><br/>
 * Created: 24.02.2010 10:24:38
 * @since 0.6.0
 * @author Volker Bergmann
 */
public abstract class FileContentGeneratorTest extends GeneratorTest {

	protected static final File ROOT_DIR = new File("target" + File.separator + "filetest");
	protected static final File SUB_DIR = new File(ROOT_DIR, "sub");
	protected static final File ROOT_DIR_FILE = new File(ROOT_DIR, "fr.txt");
	protected static final File SUB_DIR_FILE = new File(SUB_DIR, "fs.txt");
	protected static final String ROOT_DIR_FILE_CONTENT = "rfc";
	protected static final String SUB_DIR_FILE_CONTENT = "sfc";
	protected static final byte[] ROOT_DIR_FILE_CONTENT_ARRAY = ROOT_DIR_FILE_CONTENT.getBytes();
	protected static final byte[] SUB_DIR_FILE_CONTENT_ARRAY = SUB_DIR_FILE_CONTENT.getBytes();

	protected void createTestFolders() throws IOException {
		FileUtil.ensureDirectoryExists(ROOT_DIR);
		FileUtil.ensureDirectoryExists(SUB_DIR);
	    IOUtil.writeTextFile(ROOT_DIR_FILE.getAbsolutePath(), ROOT_DIR_FILE_CONTENT);
		IOUtil.writeTextFile(SUB_DIR_FILE.getAbsolutePath(), SUB_DIR_FILE_CONTENT);
    }

	protected void removeTestFolders() {
	    FileUtil.deleteIfExists(SUB_DIR_FILE);
	    FileUtil.deleteIfExists(SUB_DIR);
	    FileUtil.deleteIfExists(ROOT_DIR_FILE);
	    FileUtil.deleteIfExists(ROOT_DIR);
    }

}

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

package org.databene.benerator.file;

import java.io.File;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.sample.NonNullSampleGenerator;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.FileUtil;
import org.databene.commons.IOUtil;

/**
 * Generates {@link File} objects which represent files and/or directories in a parent directory.<br/><br/>
 * Created: 24.02.2010 10:47:44
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class FileGenerator extends NonNullSampleGenerator<File> { 
	
	private String rootUri;
	private String filter;
	private boolean recursive;
	private boolean folders;
	private boolean files;
	
	public FileGenerator() {
	    this(".", null, false, false, true);
    }
	
	public FileGenerator(String rootUri, String filter, boolean recursive, boolean files, boolean folders) {
		super(File.class);
	    this.rootUri = rootUri;
	    this.filter = filter;
	    this.recursive = recursive;
	    this.folders = folders;
	    this.files = files;
    }
	
	// properties ------------------------------------------------------------------------------------------------------

	public void setRootUri(String rootUri) {
    	this.rootUri = rootUri;
    }

	public void setFilter(String filter) {
    	this.filter = filter;
    }

	public void setRecursive(boolean recursive) {
    	this.recursive = recursive;
    }

	public void setFolders(boolean folders) {
    	this.folders = folders;
    }

	public void setFiles(boolean files) {
    	this.files = files;
    }

	public void setContext(Context context) {
	    this.context = (BeneratorContext) context;
    }
	
	// implementation --------------------------------------------------------------------------------------------------
	
	@Override
	public void init(GeneratorContext context) {
		assertNotInitialized();
    	try {
            String baseUri = IOUtil.resolveRelativeUri(rootUri, context.getContextUri());
            File baseFile = new File(baseUri);
			setValues(FileUtil.listFiles(baseFile, filter, recursive, files, folders));
            super.init(context);
        } catch (Exception e) {
            throw new ConfigurationError(e);
        }
	}

}

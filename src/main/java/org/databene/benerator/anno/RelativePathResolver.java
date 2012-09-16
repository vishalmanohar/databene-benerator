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

package org.databene.benerator.anno;

import java.io.File;

/**
 * {@link PathResolver} implementation which is based on a base path and appends the test classes' package name 
 * and finally the resource name (or path) itself to construct the resolved path.<br/><br/>
 * Created: 12.12.2011 13:16:56
 * @since 0.7.4
 * @author Volker Bergmann
 */
public class RelativePathResolver implements PathResolver {
	
	private String basePath;

	public RelativePathResolver() {
		this(".");
	}

	public RelativePathResolver(String basePath) {
		this.basePath = basePath;
	}

	public String getPathFor(String uri, Class<?> testClass) {
		char sep = File.separatorChar;
		return basePath + sep + testClass.getPackage().getName().replace('.', sep) + sep + normalizePath(uri);
	}
	
	private String normalizePath(String path) {
		return path.replace('/', File.separatorChar).replace('\\', File.separatorChar);
	}
	
}

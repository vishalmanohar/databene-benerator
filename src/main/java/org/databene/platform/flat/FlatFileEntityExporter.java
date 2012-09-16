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

package org.databene.platform.flat;

import org.databene.platform.fixedwidth.FixedWidthEntityExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Inherits the new FixedWidthEntityExporter class emitting a deprecation warning..<br/><br/>
 * Created: 05.08.2011 10:38:10
 * @since 0.7.0
 * @author Volker Bergmann
 * @deprecated Replaced with class {@link FixedWidthEntityExporter}
 */
@Deprecated
public class FlatFileEntityExporter extends FixedWidthEntityExporter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FlatFileEntityExporter.class);

	public FlatFileEntityExporter() {
		super();
		reportDeprecation();
	}

	public FlatFileEntityExporter(String uri, String encoding, String columnFormatList) {
		super(uri, encoding, columnFormatList);
		reportDeprecation();
	}

	public FlatFileEntityExporter(String uri, String columnFormatList) {
		super(uri, columnFormatList);
		reportDeprecation();
	}

	private void reportDeprecation() {
		LOGGER.warn(getClass() + " has been deprecated and will not be supported in future releases. " +
				"Use org.databene.platform.fixedwidth.FixedWidthEntityExporter instead");
	}

}

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

package org.databene.benerator.main;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.benerator.file.XMLFileGenerator;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.ArrayUtil;
import org.databene.commons.IOUtil;
import org.databene.commons.ui.ConsoleInfoPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class for generating XML files from the command line.<br/><br/>
 * Created: 28.03.2008 16:52:49
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class XmlCreator {
    
    private static final Logger logger = LoggerFactory.getLogger(XmlCreator.class);

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            printHelp();
            System.exit(-1);
        }
        String schemaUri = args[0];
        String root = args[1];
        String pattern = args[2];
        long fileCount = 1;
        if (args.length >= 4)
        	fileCount = Long.parseLong(args[3]);
        String[] propertiesFiles = (args.length > 4 ? ArrayUtil.copyOfRange(args, 4, args.length - 4) : new String[0]);
        createXMLFiles(schemaUri, root, pattern, fileCount, propertiesFiles);
    }

	public static void createXMLFiles(String schemaUri, String root,
			String pattern, long fileCount, String[] propertiesFiles) {
		logParams(schemaUri, root, pattern, fileCount);
        long start = System.currentTimeMillis();
        BeneratorContext context = new DefaultBeneratorContext(IOUtil.getParentUri(schemaUri));
        XMLFileGenerator fileGenerator = new XMLFileGenerator(schemaUri, root, pattern, propertiesFiles);
        fileGenerator.init(context);
        try {
	        for (long i = 0; i < fileCount; i++) {
	        	ProductWrapper<File> file = fileGenerator.generate(new ProductWrapper<File>());
	            if (file == null)
	            	throw new RuntimeException("Unable to create the expected number of files. " +
	            			"Created " + i + " of " + fileCount + " files");
	            logger.info("created file: " + file);
	        }
        } finally {
        	fileGenerator.close();
        }
        long duration = System.currentTimeMillis() - start;
        logger.info("Finished after " + duration + " ms");
	}

	private static void logParams(String schemaUri, String root, String pattern, long fileCount) {
		if (logger.isDebugEnabled()) {
        	if (fileCount > 1)
        		logger.debug("Creating " + fileCount + " XML files for schema " + schemaUri + " with root " + root + " and pattern " + pattern);
        	else
        		logger.debug("Creating XML file " + MessageFormat.format(pattern, fileCount) + " for schema " + schemaUri + " with root " + root);
        }
	}

    private static void printHelp() {
    	ConsoleInfoPrinter.printHelp(
        	"Invalid parameters",
        	"parameters: schemaUri root fileNamePattern [count [propertiesFilenames]]"
        );
    }
    
}

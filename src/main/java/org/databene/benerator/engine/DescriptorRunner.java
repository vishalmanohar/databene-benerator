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

package org.databene.benerator.engine;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.databene.benerator.BeneratorFactory;
import org.databene.benerator.consumer.FileExporter;
import org.databene.benerator.engine.parser.xml.BeneratorParseContext;
import org.databene.commons.IOUtil;
import org.databene.commons.RoundedNumberFormat;
import org.databene.commons.converter.ConverterManager;
import org.databene.commons.time.ElapsedTimeFormatter;
import org.databene.commons.xml.XMLUtil;
import org.databene.profile.Profiler;
import org.databene.profile.Profiling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parses and executes a benerator descriptor file.<br/>
 * <br/>
 * Created at 26.02.2009 15:51:59
 * @since 0.5.8
 * @author Volker Bergmann
 */

public class DescriptorRunner implements ResourceManager {

    public static final String LOCALE_VM_PARAM = "benerator.locale";
	
	private static final Logger logger = LoggerFactory.getLogger(DescriptorRunner.class);

	// attributes ------------------------------------------------------------------------------------------------------

	private String uri;

	private BeneratorContext context;

	BeneratorFactory factory;
	private List<String> generatedFiles;
	
	private ResourceManagerSupport resourceManager = new ResourceManagerSupport();
	long startTime = 0;

	private boolean closingResources;

	
	// constructor -----------------------------------------------------------------------------------------------------
	
	/* This constructor easily causes conflicts between two accidentally different contexts 
	public DescriptorRunner(String uri) {
		this(uri, new BeneratorContext(SystemInfo.getCurrentDir()));
	}
	*/
	public DescriptorRunner(String uri, BeneratorContext context) {
		this.uri = uri;
		this.context = context;
		this.factory = BeneratorFactory.getInstance();
		this.generatedFiles = new ArrayList<String>();
		this.closingResources = true;
		ConverterManager.getInstance().setContext(context);
	}
	
	// interface -------------------------------------------------------------------------------------------------------
	
	public BeneratorContext getContext() {
		return context;
	}
	
	public void setClosingResources(boolean closingResources) {
		this.closingResources = closingResources;
	}

    public void run() throws IOException {
    	Runtime runtime = Runtime.getRuntime();
		BeneratorShutdownHook hook = new BeneratorShutdownHook(this);
		runtime.addShutdownHook(hook);
		try {
			runWithoutShutdownHook();
		} finally {
			runtime.removeShutdownHook(hook);
		}
	}

	public void runWithoutShutdownHook() throws IOException {
		execute(parseDescriptorFile());
	}

	public BeneratorRootStatement parseDescriptorFile() throws IOException {
		Document document = XMLUtil.parse(uri);
	    Element root = document.getDocumentElement();
	    BeneratorParseContext parsingContext = factory.createParseContext(resourceManager);
	    BeneratorRootStatement statement = (BeneratorRootStatement) parsingContext.parseElement(root, null);
		// prepare system
		generatedFiles = new ArrayList<String>();
		context.setContextUri(IOUtil.getParentUri(uri));
	    return statement;
    }
	
	public void execute(BeneratorRootStatement rootStatement) {
	    try {
	    	startTime = System.currentTimeMillis();

			// run AST
			rootStatement.execute(context);
			
			// calculate and print statistics
			long elapsedTime = java.lang.System.currentTimeMillis() - startTime;
			if (closingResources)
				resourceManager.close();
			StringBuilder message = new StringBuilder("Created a total of ")
				.append(BeneratorMonitor.INSTANCE.getTotalGenerationCount()).append(" entities ");
			if (elapsedTime != 0) {
	            long throughput = BeneratorMonitor.INSTANCE.getTotalGenerationCount() * 3600000L / elapsedTime;
	            message.append("in ")
	            	.append(ElapsedTimeFormatter.format(elapsedTime))
	            	.append(" (~")
					.append(RoundedNumberFormat.format(throughput, 0))
					.append(" p.h.)");
            }
			logger.info(message.toString());
			if (Profiling.isEnabled())
				Profiler.defaultInstance().printSummary();
			List<String> generations = getGeneratedFiles();
			if (generations.size() > 0)
				logger.info("Generated file(s): " + generations);
		} finally {
			context.close();
		}
    }

	public List<String> getGeneratedFiles() {
		return generatedFiles;
	}

	// ResourceManager interface implementation ------------------------------------------------------------------------

	public boolean addResource(Closeable resource) {
		if (!resourceManager.addResource(resource))
			return false;
		else if (resource instanceof FileExporter)
			generatedFiles.add(((FileExporter) resource).getUri());
	    return true;
    }

    public void close() {
	    resourceManager.close();
    }
    
	// java.lang.Object overrides --------------------------------------------------------------------------------------

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}

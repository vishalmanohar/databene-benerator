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

package org.databene.benerator.engine.parser.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.databene.benerator.DefaultPlatformDescriptor;
import org.databene.benerator.PlatformDescriptor;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.statement.ImportStatement;
import org.databene.commons.ArrayBuilder;
import org.databene.commons.BeanUtil;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ExceptionUtil;
import org.databene.commons.StringUtil;
import org.databene.webdecs.xml.XMLElementParser;
import org.w3c.dom.Element;
import static org.databene.benerator.engine.DescriptorConstants.*;

/**
 * Parses an &lt;import&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 00:53:06
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ImportParser extends AbstractBeneratorDescriptorParser {
	
	private static final Set<String> OPTIONAL_ATTRIBUTES = CollectionUtil.toSet(
		ATT_CLASS, ATT_DEFAULTS, ATT_DOMAINS, ATT_PLATFORMS);

	public ImportParser() {
	    super(EL_IMPORT, null, OPTIONAL_ATTRIBUTES);
    }

	@Override
	public ImportStatement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
		// check syntax
		assertAtLeastOneAttributeIsSet(element, ATT_DEFAULTS, ATT_DOMAINS, ATT_PLATFORMS, ATT_CLASS);
		
		// prepare parsing
		ArrayBuilder<String> classImports = new ArrayBuilder<String>(String.class); 
		ArrayBuilder<String> domainImports = new ArrayBuilder<String>(String.class); 
		
		// defaults import
		boolean defaults = ("true".equals(element.getAttribute("defaults")));
		
		// check class import
		String attribute = element.getAttribute("class");
		if (!StringUtil.isEmpty(attribute))
			classImports.add(attribute);
		
		// (multiple) domain import
		attribute = element.getAttribute("domains");
		if (!StringUtil.isEmpty(attribute))
			domainImports.addAll(StringUtil.trimAll(StringUtil.tokenize(attribute, ',')));
		
		// (multiple) platform import
		attribute = element.getAttribute("platforms");
		
		List<PlatformDescriptor> platformImports = null; 
		if (!StringUtil.isEmpty(attribute))
			platformImports = importPlatforms(StringUtil.trimAll(attribute.split(",")), context);
		
		return new ImportStatement(defaults, classImports.toArray(), domainImports.toArray(), platformImports);
	}

	private List<PlatformDescriptor> importPlatforms(String[] platformNames, BeneratorParseContext context) {
		List<PlatformDescriptor> platforms = new ArrayList<PlatformDescriptor>(platformNames.length);
		for (String platformName : platformNames) {
			PlatformDescriptor platformDescriptor = createPlatformDescriptor(platformName);
			for (XMLElementParser<Statement> parser : platformDescriptor.getParsers())
				context.addParser(parser);
			platforms.add(platformDescriptor);
		}
		return platforms;
	}

	private PlatformDescriptor createPlatformDescriptor(String platformName) {
		String platformPackage = (platformName.indexOf('.') < 0 ? "org.databene.platform." + platformName : platformName);
		String descriptorClassName = platformPackage + ".PlatformDescriptor";
		try {
			// if there is a platform descriptor, then use it
			return (PlatformDescriptor) BeanUtil.newInstance(descriptorClassName);
		} catch (RuntimeException e) {
			if (ExceptionUtil.getRootCause(e) instanceof ClassNotFoundException) { // TODO test
				DefaultPlatformDescriptor descriptor = new DefaultPlatformDescriptor(platformPackage);
				return descriptor;
			} else
				throw e;
		}
	}

}

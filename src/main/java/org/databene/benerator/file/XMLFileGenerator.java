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

package org.databene.benerator.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.statement.IncludeStatement;
import org.databene.benerator.factory.MetaGeneratorFactory;
import org.databene.benerator.primitive.IncrementGenerator;
import org.databene.benerator.util.UnsafeGenerator;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.benerator.wrapper.WrapperFactory;
import org.databene.commons.ConfigurationError;
import org.databene.commons.IOUtil;
import org.databene.commons.SystemInfo;
import org.databene.commons.converter.MessageConverter;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.data.Entity;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.data.Uniqueness;
import org.databene.platform.xml.XMLEntityExporter;
import org.databene.platform.xml.XMLSchemaDescriptorProvider;

/**
 * Generates XML files.<br/>
 * <br/>
 * @author Volker Bergmann
 */
public class XMLFileGenerator extends UnsafeGenerator<File> {
	
	private String schemaUri;
    private String encoding;
    private String root;
    private String[] propertiesFiles;
    private String filenamePattern;
    private Generator<String> fileNameGenerator;
    private Generator<?> contentGenerator;
    
    public XMLFileGenerator(String schemaUri, String root, String filenamePattern, String... propertiesFiles) {
    	this.schemaUri = schemaUri;
        this.encoding = SystemInfo.getFileEncoding();
        this.filenamePattern = filenamePattern;
        this.propertiesFiles = propertiesFiles;
        this.root = root;
    }

    public Class<File> getGeneratedType() {
    	return File.class;
    }

    @Override
    public void init(GeneratorContext context) {
    	BeneratorContext beneratorContext = (BeneratorContext) context; 
        // parse schema
        XMLSchemaDescriptorProvider xsdProvider = new XMLSchemaDescriptorProvider(schemaUri, beneratorContext);
		beneratorContext.getDataModel().addDescriptorProvider(xsdProvider);
        // set up file name generator
        this.fileNameGenerator = WrapperFactory.applyConverter(
                new IncrementGenerator(), 
                new MessageConverter(filenamePattern, Locale.US));
        // parse properties files
        try {
        for (String propertiesFile : propertiesFiles)
            IncludeStatement.includeProperties(propertiesFile, beneratorContext);
        } catch (IOException e) {
        	throw new InvalidGeneratorSetupException(e);
        }
        // set up content generator
        TypeDescriptor rootDescriptor = beneratorContext.getDataModel().getTypeDescriptor(root);
        if (rootDescriptor == null)
            throw new ConfigurationError("Type '" + root + "' not found in schema: " + schemaUri);
		contentGenerator = MetaGeneratorFactory.createTypeGenerator(
				rootDescriptor, root, false, Uniqueness.NONE, beneratorContext);
        contentGenerator.init(context);
        super.init(context);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public ProductWrapper<File> generate(ProductWrapper<File> wrapper) {
		ProductWrapper tmp = contentGenerator.generate(new ProductWrapper());
        if (tmp == null)
        	return null;
        return wrapper.wrap(persistContent(tmp.unwrap()));
    }

    private File persistContent(Object content) {
        File file = new File(fileNameGenerator.generate(new ProductWrapper<String>()).unwrap());
        if (content instanceof Entity)
            persistRootEntity((Entity) content, file);
        else
            persistRootObject(content, file);
        return file;
    }

    private void persistRootEntity(Entity entity, File file) {
        //entity.setComponentValue("xmlns", "http://databene.org/shop-0.5.1.xsd");
        entity.setComponent("elementFormDefault", "unqualified");
        XMLEntityExporter exporter = null;
        try {
            exporter = new XMLEntityExporter(file.getAbsolutePath(), encoding);
            process(entity, exporter);
        } finally {
            if (exporter != null)
                exporter.close();
        }
    }

    private void process(Entity entity, XMLEntityExporter exporter) {
        exporter.startProductConsumption(entity);
        for (Object component : entity.getComponents().values()) {
        	if (component == null)
        		continue;
            if (component instanceof Entity)
                process((Entity) component, exporter);
            else if (component.getClass().isArray()) {
                Object[] array = (Object[]) component;
                for (Object element : array)
                    if (element instanceof Entity)
                        process((Entity) element, exporter);
            }
        }
        exporter.finishProductConsumption(entity);
    }

    private void persistRootObject(Object content, File file) {
        PrintWriter printer = null;
        try {
            printer = XMLUtil.createXMLFile(file.getAbsolutePath(), encoding);
            printer.println("<" + root + ">" + content + "</" + root + ">");
        } catch (FileNotFoundException e) {
            throw new ConfigurationError(e);
        } catch (UnsupportedEncodingException e) {
            throw new ConfigurationError(e);
        } finally {
            IOUtil.close(printer);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + filenamePattern + ']';
    }

}

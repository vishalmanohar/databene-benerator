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

package org.databene.platform.fixedwidth;

import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.commons.ArrayUtil;
import org.databene.commons.Converter;
import org.databene.commons.Escalator;
import org.databene.commons.LoggerEscalator;
import org.databene.commons.SystemInfo;
import org.databene.commons.bean.ArrayPropertyExtractor;
import org.databene.commons.converter.ArrayConverter;
import org.databene.commons.converter.ConverterChain;
import org.databene.commons.converter.NoOpConverter;
import org.databene.commons.format.PadFormat;
import org.databene.document.fixedwidth.FixedWidthColumnDescriptor;
import org.databene.document.fixedwidth.FixedWidthLineSource;
import org.databene.document.fixedwidth.FixedWidthUtil;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.FileBasedEntitySource;
import org.databene.platform.array.Array2EntityConverter;
import org.databene.webdecs.DataIterator;
import org.databene.webdecs.DataSource;
import org.databene.webdecs.util.ConvertingDataIterator;

/**
 * Reads Entities from a fixed-width file.<br/>
 * <br/>
 * Created at 07.11.2008 18:18:24
 * @since 0.5.6
 * @author Volker Bergmann
 */
public class FixedWidthEntitySource extends FileBasedEntitySource {

	private static final Escalator escalator = new LoggerEscalator();
	
    private String encoding;
    private String entityTypeName;
    private ComplexTypeDescriptor entityDescriptor;
    private FixedWidthColumnDescriptor[] descriptors;
    private String lineFilter;
    private boolean initialized;
    
    private Converter<String, String> preprocessor;
    protected DataSource<String[]> source;
    protected Converter<String[], Entity> converter;

    public FixedWidthEntitySource() {
        this(null, null, SystemInfo.getFileEncoding(), null);
    }

    public FixedWidthEntitySource(String uri, ComplexTypeDescriptor entityDescriptor, 
    		String encoding, String lineFilter, FixedWidthColumnDescriptor ... descriptors) {
        this(uri, entityDescriptor, new NoOpConverter<String>(), encoding, lineFilter, descriptors);
    }

    public FixedWidthEntitySource(String uri, ComplexTypeDescriptor entityDescriptor, 
    		Converter<String, String> preprocessor, String encoding, String lineFilter, 
    		FixedWidthColumnDescriptor ... descriptors) {
        super(uri);
        this.encoding = encoding;
        this.entityDescriptor = entityDescriptor;
        this.descriptors = descriptors;
        this.preprocessor = preprocessor;
        this.initialized = false;
        this.lineFilter = lineFilter;
    }
    
    // properties ------------------------------------------------------------------------------------------------------

    public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getEntity() {
        return entityTypeName;
    }

    public void setEntity(String entity) {
        this.entityTypeName = entity;
    }

    /**
     * @deprecated use {@link #setColumns(String)}
     */
    @Deprecated
	public void setProperties(String properties) {
    	escalator.escalate("The property 'properties' of class " + getClass() + "' has been renamed to 'columns'. " +
    			"Please fix the property name in your configuration", this.getClass(), "setProperties()");
        setColumns(properties);
    }

    public void setColumns(String columns) {
        this.descriptors = FixedWidthUtil.parseColumnsSpec(columns);
    }
    
    // Iterable interface ----------------------------------------------------------------------------------------------

    public void setLineFilter(String lineFilter) {
    	this.lineFilter = lineFilter;
    }

	@Override
    public Class<Entity> getType() {
    	if (!initialized)
    		init();
    	return Entity.class;
    }
    
    public DataIterator<Entity> iterator() {
        if (!initialized)
            init();
        return new ConvertingDataIterator<String[], Entity>(this.source.iterator(), converter);
    }
    
    // private helpers -------------------------------------------------------------------------------------------------
    
    private void init() {
    	if (this.entityDescriptor == null)
    		this.entityDescriptor = new ComplexTypeDescriptor(entityTypeName, context.getLocalDescriptorProvider());
    	if (ArrayUtil.isEmpty(descriptors))
    		throw new InvalidGeneratorSetupException("Missing column descriptors. " +
    				"Use the 'columns' property of the " + getClass().getSimpleName() + " to define them.");
        this.source = createSource();
        this.converter = createConverter();
    }
    
    private DataSource<String[]> createSource() {
        PadFormat[] formats = ArrayPropertyExtractor.convert(descriptors, "format", PadFormat.class);
        return new FixedWidthLineSource(resolveUri(), formats, true, encoding, lineFilter);
    }

    @SuppressWarnings("unchecked")
    private Converter<String[], Entity> createConverter() {
        String[] featureNames = ArrayPropertyExtractor.convert(descriptors, "name", String.class);
        Array2EntityConverter a2eConverter = new Array2EntityConverter(entityDescriptor, featureNames, true);
        Converter<String[], String[]> aConv = new ArrayConverter<String, String>(String.class, String.class, preprocessor);
        Converter<String[], Entity> converter = new ConverterChain<String[], Entity>(aConv, a2eConverter);
        return converter;
    }

}

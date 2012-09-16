/*
 * (c) Copyright 2007-2011 by Volker Bergmann. All rights reserved.
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

import org.databene.model.data.Entity;
import org.databene.model.data.ComponentAccessor;
import org.databene.document.fixedwidth.FixedWidthColumnDescriptor;
import org.databene.benerator.consumer.TextFileExporter;
import org.databene.commons.*;
import org.databene.commons.converter.AccessingConverter;
import org.databene.commons.converter.ConverterChain;
import org.databene.commons.converter.FormatFormatConverter;
import org.databene.commons.format.Alignment;
import org.databene.commons.format.PadFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParsePosition;
import java.text.ParseException;

/**
 * Exports Entities to fixed-width files.<br/>
 * <br/>
 * Created: 26.08.2007 06:17:41
 * @author Volker Bergmann
 */
public class FixedWidthEntityExporter extends TextFileExporter {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(FixedWidthEntityExporter.class);

    private Converter<Entity, String> converters[];

    public FixedWidthEntityExporter() {
        this("export.fcw", null);
    }

    public FixedWidthEntityExporter(String uri, String columnFormatList) {
        this(uri, null, columnFormatList);
    }

    public FixedWidthEntityExporter(String uri, String encoding, String columnFormatList) {
        super(uri, encoding, null);
        this.uri = uri;
        setColumns(columnFormatList);
        setDecimalPattern("0.##");
    }

    // properties ------------------------------------------------------------------------------------------------------

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void setColumns(String columnFormatList) {
        if (columnFormatList == null) {
            converters = null;
            return;
        }
        try {
            String[] columnFormats = StringUtil.tokenize(columnFormatList, ',');
            this.converters = new Converter[columnFormats.length];
            for (int i = 0; i < columnFormats.length; i++) {
                String columnFormat = columnFormats[i];
                int lbIndex = columnFormat.indexOf('[');
                if (lbIndex < 0)
                    throw new ConfigurationError("'[' expected in column format descriptor '" + columnFormat + "'");
                int rbIndex = columnFormat.indexOf(']');
                if (rbIndex < 0)
                    throw new ConfigurationError("']' expected in column format descriptor '" + columnFormat + "'");
                String columnName = columnFormat.substring(0, lbIndex);
                // parse width
                ParsePosition pos = new ParsePosition(lbIndex + 1);
                int width = (int) ParseUtil.parseNonNegativeInteger(columnFormat, pos);
                // parse fractionDigits
                int minFractionDigits = 0;
                int maxFractionDigits = 2;
                if (pos.getIndex() < rbIndex && columnFormat.charAt(pos.getIndex()) == '.') {
                    pos.setIndex(pos.getIndex() + 1);
                    minFractionDigits = (int) ParseUtil.parseNonNegativeInteger(columnFormat, pos);
                    maxFractionDigits = minFractionDigits;
                }
                // parse alignment
                Alignment alignment = Alignment.LEFT;
                if (pos.getIndex() < rbIndex) {
                    char alignmentCode = columnFormat.charAt(pos.getIndex());
                    switch (alignmentCode) {
                        case 'l' : alignment = Alignment.LEFT; break;
                        case 'r' : alignment = Alignment.RIGHT; break;
                        case 'c' : alignment = Alignment.CENTER; break;
                        default: throw new ConfigurationError("Illegal alignment code '" + alignmentCode + "'" +
                        		" in colun format descriptor '" + columnFormat + "'");
                    }
                    pos.setIndex(pos.getIndex() + 1);
                }
                // parse pad char
                char padChar = ' ';
                if (pos.getIndex() < rbIndex) {
                    padChar = columnFormat.charAt(pos.getIndex());
                    pos.setIndex(pos.getIndex() + 1);
                }
                assert pos.getIndex() == rbIndex;
                FixedWidthColumnDescriptor descriptor = new FixedWidthColumnDescriptor(columnName, width, alignment, padChar);
                PadFormat format = new PadFormat(descriptor.getWidth(), minFractionDigits, maxFractionDigits, descriptor.getAlignment(), padChar);
                ConverterChain<Entity, String> chain = new ConverterChain<Entity, String>();
                chain.addComponent(new AccessingConverter<Entity, Object>(Entity.class, Object.class, new ComponentAccessor(descriptor.getName())));
                if (format.getMinimumFractionDigits() == 0)
                	chain.addComponent(plainConverter);
				chain.addComponent(new FormatFormatConverter(String.class, format, true));
                this.converters[i] = chain;
            }
        } catch (ParseException e) {
            throw new ConfigurationError("Invalid column definition: " + columnFormatList, e);
        }
    }

    // Consumer interface ----------------------------------------------------------------------------------------------

    @Override
	public void flush() {
        if (printer != null)
            printer.flush();
    }

    @Override
	public void close() {
        IOUtil.close(printer);
    }
    
    // Callback methods for TextFileExporter ---------------------------------------------------------------------------
    
	@Override
	protected void postInitPrinter(Object object) {
        if (this.converters == null)
            throw new ConfigurationError("Property 'columns' not set on bean " + getClass().getName());
	}

	@Override
	protected void startConsumingImpl(Object object) {
        LOGGER.debug("exporting {}", object);
        if (!(object instanceof Entity))
        	throw new IllegalArgumentException("Expected Entity");
        Entity entity = (Entity) object;
        for (Converter<Entity, String> converter : converters)
            printer.print(converter.convert(entity));
        printer.print(lineSeparator);
	}

    // java.lang.Object overrrides -------------------------------------------------------------------------------------

    @Override
	public String toString() {
        return getClass().getSimpleName() + '[' + ArrayFormat.format() + ']';
    }

}

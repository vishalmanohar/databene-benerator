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

package org.databene.benerator.engine;

import java.io.IOException;

import org.databene.benerator.Generator;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.commons.converter.ConverterManager;

/**
 * Provides easy programmatic access to generators defined in an XML descriptor file.<br/><br/>
 * Created: 23.02.2010 12:06:44
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DescriptorBasedGenerator extends GeneratorProxy<Object> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
    public DescriptorBasedGenerator(String uri, String generatorName, BeneratorContext context) throws IOException {
		super(Object.class);
		ConverterManager.getInstance().setContext(context);
		DescriptorRunner descriptorRunner = new DescriptorRunner(uri, context);
		BeneratorRootStatement rootStatement = descriptorRunner.parseDescriptorFile();
		super.setSource((Generator) rootStatement.getGenerator(generatorName, context));
	}
	
}

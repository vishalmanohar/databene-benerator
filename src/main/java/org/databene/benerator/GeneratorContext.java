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

package org.databene.benerator;

import java.util.Locale;
import java.util.concurrent.ExecutorService;

import org.databene.benerator.factory.GeneratorFactory;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.Context;

/**
 * Provides configuration and variable space for {@link Generator}s.<br/><br/>
 * Created: 14.03.2010 13:14:00
 * @since 0.6.0
 * @author Volker Bergmann
 */
public interface GeneratorContext extends Context {

	// global properties -----------------------------------------------------------------------------------------------
	
    String getDefaultEncoding();
    String getDefaultLineSeparator();
	Locale getDefaultLocale();
	String getDefaultDataset();
	long getDefaultPageSize();
    String getDefaultScript();
    boolean isDefaultNull();
	char getDefaultSeparator();
	String getDefaultErrorHandler();
	String getContextUri();
	boolean isValidate();
	Long getMaxCount();

	// other features --------------------------------------------------------------------------------------------------
	
    GeneratorFactory getGeneratorFactory();
	Object getSetting(String name);
    Class<?> forName(String className);
	ExecutorService getExecutorService();
	String resolveRelativeUri(String relativeUri);

	ProductWrapper<?> getCurrentProduct();
	void setCurrentProduct(ProductWrapper<?> currentProduct);
	
}

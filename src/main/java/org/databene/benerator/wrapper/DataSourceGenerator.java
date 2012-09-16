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

package org.databene.benerator.wrapper;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.util.AbstractGenerator;
import org.databene.commons.IOUtil;
import org.databene.webdecs.DataContainer;
import org.databene.webdecs.DataIterator;
import org.databene.webdecs.DataSource;
import org.databene.webdecs.util.ThreadLocalDataContainer;

/**
 * {@link Generator} implementation which reads and forwards data from a {@link DataSource}.<br/><br/>
 * Created: 24.07.2011 08:58:09
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class DataSourceGenerator<E> extends AbstractGenerator<E> {

    private DataSource<E> source;
    private DataIterator<E> iterator;
    private ThreadLocalDataContainer<E> container = new ThreadLocalDataContainer<E>();

    // constructors ----------------------------------------------------------------------------------------------------

    public DataSourceGenerator() {
        this(null);
    }

    public DataSourceGenerator(DataSource<E> source) {
        this.source = source;
        this.iterator = null;
    }
    
    // properties ------------------------------------------------------------------------------------------------------

    public DataSource<E> getSource() {
        return source;
    }

    public void setSource(DataSource<E> source) {
        if (this.source != null)
        	throw new IllegalGeneratorStateException("Mutating an initialized generator");
        this.source = source;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

	public boolean isParallelizable() {
	    return false;
    }

	public boolean isThreadSafe() {
	    return true;
    }
    
    public Class<E> getGeneratedType() {
        return source.getType();
    }

    @Override
    public void init(GeneratorContext context) {
    	if (source == null)
    		throw new InvalidGeneratorSetupException("source", "is null");
    	super.init(context);
    }

	public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
        try {
            assertInitialized();
            if (iterator == null)
            	iterator = source.iterator(); // iterator initialized lazily to reflect context state at invocation
        	DataContainer<E> tmp = iterator.next(container.get());
            if (tmp == null) {
                IOUtil.close(iterator);
            	return null;
            }
			return wrapper.wrap(tmp.getData());
        } catch (Exception e) {
        	throw new IllegalGeneratorStateException("Generation failed: ", e);
        }
    }

	@Override
    public void reset() {
        IOUtil.close(iterator);
        iterator = null;
        super.reset();
    }

    @Override
    public void close() {
        IOUtil.close(iterator);
        super.close();
       	IOUtil.close(source);
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + source + ']';
    }

}

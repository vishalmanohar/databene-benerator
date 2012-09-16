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

import org.databene.benerator.NonNullGenerator;

/**
 * {@link GeneratorWrapper} for {@link NonNullGenerator}s.<br/><br/>
 * Created: 27.07.2011 11:30:51
 * @since 0.7.0
 * @author Volker Bergmann
 */
public abstract class NonNullGeneratorWrapper<S, P> extends GeneratorWrapper<S, P> implements NonNullGenerator<P> {

    public NonNullGeneratorWrapper(NonNullGenerator<S> source) {
        super(source);
    }

    /** Returns the source generator */
    @Override
	public NonNullGenerator<S> getSource() {
        return (NonNullGenerator<S>) super.getSource();
    }

    /** Sets the source generator */
    public void setSource(NonNullGenerator<S> source) {
        super.setSource(source);
    }
    
    protected final S generateFromNotNullSource() {
		return getSource().generate();
    }

	public final ProductWrapper<P> generate(ProductWrapper<P> wrapper) {
		P result = generate();
		return (result != null ? wrapper.wrap(result) : null);
	}

}

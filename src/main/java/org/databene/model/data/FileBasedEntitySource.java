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

package org.databene.model.data;

import org.databene.benerator.engine.BeneratorContext;

/**
 * Parent class for {@link EntitySource}s that import entities from files.<br/><br/>
 * Created: 12.03.2010 11:05:13
 * @since 0.6.0
 * @author Volker Bergmann
 */
public abstract class FileBasedEntitySource extends AbstractEntitySource {
	
	protected String uri;
	
	public FileBasedEntitySource(String uri) {
	    this(uri, null);
    }

	public FileBasedEntitySource(String uri, BeneratorContext context) {
	    this.uri = uri;
	    this.context = context;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
    
    protected String resolveUri() {
    	return context.resolveRelativeUri(uri);
    }
    
    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
	public String toString() {
        return getClass().getSimpleName() + "[" + uri + "]";
    }

}

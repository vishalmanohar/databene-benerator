/*
 * (c) Copyright 2008-2012 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive;

import org.databene.benerator.util.ThreadSafeGenerator;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.script.Script;
import org.databene.script.ScriptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates {@link Object}s based on a Script.<br/><br/>
 * Created: 29.01.2008 17:19:24
 * @since 0.4.0
 * @author Volker Bergmann
 */
public class ScriptGenerator extends ThreadSafeGenerator<Object> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptGenerator.class);

    private Script script;
    
    public ScriptGenerator(Script script) {
        this.script = script;
    }

    public Class<Object> getGeneratedType() {
	    return Object.class;
    }

	public ProductWrapper<Object> generate(ProductWrapper<Object> wrapper) {
        Object result = ScriptUtil.execute(script, context);
        LOGGER.debug("Generated: {}", result);
        return wrapper.wrap(result);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + script + ']';
    }

}

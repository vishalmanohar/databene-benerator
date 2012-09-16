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

package org.databene.benerator.engine.statement;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.factory.DescriptorUtil;
import org.databene.commons.Context;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.script.Expression;
import org.w3c.dom.Element;

/**
 * {@link Expression} which changes an {@link Entity}'s type.<br/><br/>
 * Created: 18.04.2011 14:48:57
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class MutatingTypeExpression implements Expression<ComplexTypeDescriptor> {

	private Element element;
	private String typeName;

	public MutatingTypeExpression(Element element, String typeName) {
		this.element = element;
		this.typeName = typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public boolean isConstant() {
		return true;
	}

	public ComplexTypeDescriptor evaluate(Context ctx) {
		BeneratorContext context = (BeneratorContext) ctx;
	    ComplexTypeDescriptor parent = (ComplexTypeDescriptor) context.getDataModel().getTypeDescriptor(typeName);
	    ComplexTypeDescriptor type = new ComplexTypeDescriptor(typeName, context.getLocalDescriptorProvider(), parent);
	    DescriptorUtil.parseComponentConfig(element, type, context);
	    return type;
	}

}

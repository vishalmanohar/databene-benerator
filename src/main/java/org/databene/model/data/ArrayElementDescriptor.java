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

/**
 * Describes an array element.<br/><br/>
 * Created: 30.04.2010 10:08:31
 * @since 0.6.1
 * @author Volker Bergmann
 */
public class ArrayElementDescriptor extends ComponentDescriptor {

	public ArrayElementDescriptor(int index, DescriptorProvider provider, String typeName, TypeDescriptor localType) {
	    super(String.valueOf(index), provider, typeName, localType);
    }

	public ArrayElementDescriptor(int index, DescriptorProvider provider, String typeName) {
	    super(String.valueOf(index), provider, typeName);
    }

	public ArrayElementDescriptor(int index, DescriptorProvider provider, TypeDescriptor localType) {
	    super(String.valueOf(index), provider, localType);
    }

	public int getIndex() {
		return Integer.parseInt(getName());
	}
	
	public void setIndex(int index) {
		setName(String.valueOf(index));
	}
	
}

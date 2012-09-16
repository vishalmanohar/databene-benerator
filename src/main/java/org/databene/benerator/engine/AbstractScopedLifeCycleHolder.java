/*
 * (c) Copyright 2012 by Volker Bergmann. All rights reserved.
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

/**
 * Parent class for all generation components that can connect to the life cycle of a different component 
 * (thus defining a 'scope').<br/>
 * <br/>
 * Created: 03.04.2012 19:28:48
 * @since 0.7.6
 * @author Volker Bergmann
 */
public abstract class AbstractScopedLifeCycleHolder implements ScopedLifeCycleHolder {
	
	private String scope;
	private boolean resetNeeded;
	
	public AbstractScopedLifeCycleHolder(String scope) {
		this.scope = scope;
		setResetNeeded(false);
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public boolean isResetNeeded() {
		return resetNeeded;
	}

	public void setResetNeeded(boolean resetNeeded) {
		this.resetNeeded = resetNeeded;
	}

	public void resetIfNeeded() {
		if (this.resetNeeded) {
			reset();
			setResetNeeded(false);
		}
	}

}

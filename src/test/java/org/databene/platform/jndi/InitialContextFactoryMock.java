/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.jndi;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.spi.InitialContextFactory;

import org.databene.benerator.factory.ConsumerMock;

/**
 * Helper class for mocking JNDI functionality.<br/><br/>
 * Created: 21.10.2009 20:03:01
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class InitialContextFactoryMock implements InitialContextFactory, Context {
	
	public Context getInitialContext(Hashtable<?, ?> environment) {
		return this;
    }
	
	public Object addToEnvironment(String s, Object obj) {
        return null;
    }

	public void bind(Name name, Object obj) {
    }

	public void bind(String s, Object obj) {
    }

	public void close() {
    }

	public Name composeName(Name name, Name name1) {
        return null;
    }

	public String composeName(String s, String s1) {
        return null;
    }

	public Context createSubcontext(Name name) {
        return null;
    }

	public Context createSubcontext(String s) {
        return null;
    }

	public void destroySubcontext(Name name) {
    }

	public void destroySubcontext(String s) {
    }

	public Hashtable<?, ?> getEnvironment() {
        return null;
    }

	public String getNameInNamespace() {
        return null;
    }

	public NameParser getNameParser(Name name) {
        return null;
    }

	public NameParser getNameParser(String s) {
        return null;
    }

	public NamingEnumeration<NameClassPair> list(Name name) {
        return null;
    }

	public NamingEnumeration<NameClassPair> list(String s) {
        return null;
    }

	public NamingEnumeration<Binding> listBindings(Name name) {
        return null;
    }

	public NamingEnumeration<Binding> listBindings(String s) {
        return null;
    }

	public Object lookup(Name name) {
        return new ConsumerMock();
    }

	public Object lookup(String s) {
        return new ConsumerMock();
    }

	public Object lookupLink(Name name) {
        return null;
    }

	public Object lookupLink(String s) {
        return null;
    }

	public void rebind(Name name, Object obj) {
    }

	public void rebind(String s, Object obj) {
    }

	public Object removeFromEnvironment(String s) {
        return null;
    }

	public void rename(Name name, Name name1) {
    }

	public void rename(String s, String s1) {
    }

	public void unbind(Name name) {
    }

	public void unbind(String s) {
    }
	
}

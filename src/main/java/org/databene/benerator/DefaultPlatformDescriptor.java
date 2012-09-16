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

package org.databene.benerator;

import java.util.ArrayList;
import java.util.List;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.Statement;
import org.databene.webdecs.xml.XMLElementParser;

/**
 * Default implementation of the {@link PlatformDescriptor} interface.<br/><br/>
 * Created: 07.12.2011 18:58:25
 * @since 0.7.4
 * @author Volker Bergmann
 */
public class DefaultPlatformDescriptor implements PlatformDescriptor {
	
	private String rootPackage;
	private List<XMLElementParser<Statement>> parsers;

	public DefaultPlatformDescriptor(String rootPackage) {
		this.rootPackage = rootPackage;
		this.parsers = new ArrayList<XMLElementParser<Statement>>();
	}

	public List<XMLElementParser<Statement>> getParsers() {
		return parsers;
	}
	
	public void addParser(XMLElementParser<Statement> parser) {
		parsers.add(parser);
	}

	public void init(BeneratorContext context) {
		context.importPackage(rootPackage);
	}

}

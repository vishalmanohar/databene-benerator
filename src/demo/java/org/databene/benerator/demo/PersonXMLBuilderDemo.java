/*
 * (c) Copyright 2006-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.demo;

import org.databene.script.ScriptedDocumentWriter;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.benerator.file.FileBuilder;
import org.databene.domain.person.PersonGenerator;
import org.databene.domain.person.Person;
import org.databene.commons.IOUtil;

import java.io.*;

/**
 * Demonstrates how to use a FreeMarker script for formatting JavaBeans in custom file formats, e.g. XML.<br/>
 * <br/>
 * Created: 07.06.2007 12:04:39
 */
public class PersonXMLBuilderDemo {
    private static final String FILE_NAME = "target/persons.xml";
    private static final int LENGTH = 10;

    public static void main(String[] args) throws IOException {
        Writer out = null;
        try {
            //out = new BufferedWriter(new FileWriter(FILE_NAME));
            out = new OutputStreamWriter(System.out);
            ScriptedDocumentWriter<Person> writer = new ScriptedDocumentWriter<Person>(
                out,
                "org/databene/benerator/xmlHeader.ftl",
                "org/databene/benerator/xmlPart.ftl",
                "org/databene/benerator/xmlFooter.ftl"
            );
            System.out.println("Running...");
            long startMillis = System.currentTimeMillis();
            PersonGenerator generator = new PersonGenerator();
            generator.init(new DefaultBeneratorContext());
			FileBuilder.build(generator, LENGTH, writer);
            long elapsedTime = System.currentTimeMillis() - startMillis;
            System.out.println("Created file " + FILE_NAME + " with " + LENGTH + " entries " +
                    "within " + (elapsedTime / 1000) + "s (" + (LENGTH * 1000L / elapsedTime) + 
                    " entries per second)");
        } finally {
            IOUtil.close(out);
        }
    }
}

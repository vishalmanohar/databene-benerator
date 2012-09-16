/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine.statement;

import java.util.List;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.BeneratorMonitor;
import org.databene.benerator.engine.Statement;
import org.databene.profile.Profiler;
import org.databene.profile.Profiling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {Task} implementation that acts as a proxy to another tasks, forwards calls to it, 
 * measures execution times and logs them.<br/><br/>
 * Created at 23.07.2009 06:55:46
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class TimedGeneratorStatement extends StatementProxy {
	
	private static final Logger logger = LoggerFactory.getLogger(TimedGeneratorStatement.class);

	private String name;
	List<String> profilerPath;
	private boolean logging;
	
    public TimedGeneratorStatement(String name, Statement realStatement, List<String> profilerPath, boolean logging) {
    	super(realStatement);
    	this.name = name;
    	this.profilerPath = profilerPath;
    	this.logging = logging;
    }

    @Override
    public boolean execute(BeneratorContext context) {
    	long c0 = BeneratorMonitor.INSTANCE.getTotalGenerationCount();
	    long t0 = System.currentTimeMillis();
		boolean result = super.execute(context);
		long dc = BeneratorMonitor.INSTANCE.getTotalGenerationCount() - c0;
		long dt = System.currentTimeMillis() - t0;
		if (logging) {
			if (dc == 0)
				logger.info("No data created for '" + name + "' setup");
			else if (dt > 0)
				logger.info("Created " + dc + " data sets from '"
						+ name + "' setup in " + dt + " ms ("
						+ (dc * 1000 / dt) + "/s)");
			else
				logger.info("Created " + dc + " '" + name + "' data set(s)");
		}
		if (Profiling.isEnabled())
			Profiler.defaultInstance().addSample(profilerPath, dt);
    	return result;
    }

}

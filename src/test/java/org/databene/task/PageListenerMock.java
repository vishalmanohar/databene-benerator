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

package org.databene.task;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Mock for the {@link PageListener} interface to be used for testing.<br/><br/>
 * Created: 26.10.2009 07:48:08
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class PageListenerMock implements PageListener {
	
	public int id;
	
	public static volatile AtomicInteger startCount = new AtomicInteger();
	public static volatile AtomicInteger finishCount = new AtomicInteger();

	public PageListenerMock(int id) {
	    this.id = id;
    }

	public void pageStarting() {
	    startCount.incrementAndGet();
    }

	public void pageFinished() {
	    finishCount.incrementAndGet();
    }

	@Override
    public int hashCode() {
	    return id;
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null || this.getClass() != obj.getClass())
		    return false;
	    PageListenerMock that = (PageListenerMock) obj;
	    return (this.id == that.id);
    }

}

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

package org.databene.benerator.util;

import java.util.Iterator;

import org.databene.commons.Context;
import org.databene.script.Expression;
import org.databene.webdecs.DataIterator;
import org.databene.webdecs.DataSource;
import org.databene.webdecs.util.DataSourceProxy;

/**
 * {@link Iterable} proxy which creates {@link Iterator}s that filter their output with a (boolean) filter expression.<br/><br/>
 * Created: 08.03.2011 11:47:20
 * @since 0.5.8
 * @author Volker Bergmann
 * @see FilterExIterator
 */
public class FilterExDataSource<E> extends DataSourceProxy<E> {

	private Expression<Boolean> filterEx;
	private Context context;

	public FilterExDataSource(DataSource<E> source, Expression<Boolean> filterEx, Context context) {
	    super(source);
	    this.filterEx = filterEx;
	    this.context = context;
    }

	@Override
	public DataIterator<E> iterator() {
		return new FilterExIterator<E>(super.iterator(), filterEx, context);
	}

}

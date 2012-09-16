/*
 * (c) Copyright 2008-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive.datetime;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.util.WrapperProvider;
import org.databene.benerator.wrapper.CompositeGenerator;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.TimeUtil;
import org.databene.model.data.Uniqueness;

/**
 * Creates DateTimes with separate date and time distribution characteristics.<br/><br/>
 * Created: 29.02.2008 18:19:55
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class DateTimeGenerator extends CompositeGenerator<Date> implements NonNullGenerator<Date> {
    
    private DayGenerator dateGenerator;
    private Generator<Long> timeOffsetGenerator;
    
    Date minDate;
    Date maxDate;
    String dateGranularity;
    Distribution dateDistribution;
    
    long minTime;
    long maxTime;
    long timeGranularity;
    Distribution timeDistribution;
	private WrapperProvider<Long> timeWrapperProvider = new WrapperProvider<Long>();
    
    public DateTimeGenerator() {
        this(
            TimeUtil.add(TimeUtil.today(), Calendar.YEAR, -1), 
            TimeUtil.today(), 
            TimeUtil.time(9, 0), 
            TimeUtil.time(17, 0));
    }

    public DateTimeGenerator(Date minDate, Date maxDate, Time minTime, Time maxTime) {
    	super(Date.class);
        setMinDate(minDate);
        setMaxDate(maxDate);
        setMinTime(minTime);
        setMaxTime(maxTime);
        setDateDistribution(SequenceManager.RANDOM_SEQUENCE);
        setTimeDistribution(SequenceManager.RANDOM_SEQUENCE);
        setDateGranularity("00-00-01");
        setTimeGranularity(TimeUtil.time(0, 1));
    }

    // properties ------------------------------------------------------------------------------------------------------

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }
    
    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public void setDateGranularity(String dateGranularity) {
    	this.dateGranularity = dateGranularity;
    }
    
    public void setDateDistribution(Distribution distribution) {
        this.dateDistribution = distribution;
    }
    
    public void setMinTime(Time minTime) {
        this.minTime = TimeUtil.millisSinceOwnEpoch(minTime);
    }
    
    public void setMaxTime(Time maxTime) {
        this.maxTime = TimeUtil.millisSinceOwnEpoch(maxTime);
    }
    
    public void setTimeGranularity(Time timeGranularity) {
        this.timeGranularity = TimeUtil.millisSinceOwnEpoch(timeGranularity);
    }
    
    public void setTimeDistribution(Distribution distribution) {
        this.timeDistribution = distribution;
    }

    // Generator interface ---------------------------------------------------------------------------------------------
    
    @Override
    public void init(GeneratorContext context) {
    	assertNotInitialized();
    	this.dateGenerator = registerComponent(
    			new DayGenerator(minDate, maxDate, dateDistribution, false));
    	dateGenerator.setGranularity(dateGranularity);
    	this.dateGenerator.init(context);
    	this.timeOffsetGenerator = registerComponent(context.getGeneratorFactory().createNumberGenerator(
    			Long.class, minTime, true, maxTime, true, timeGranularity, timeDistribution, Uniqueness.NONE));
    	this.timeOffsetGenerator.init(context);
        super.init(context);
    }

	public ProductWrapper<Date> generate(ProductWrapper<Date> wrapper) {
		Date result = generate();
		return (result != null ? wrapper.wrap(result) : null);
    }

	public Date generate() {
    	assertInitialized();
    	Date dateGeneration = dateGenerator.generate();
    	if (dateGeneration == null)
    		return null;
    	ProductWrapper<Long> timeWrapper = timeOffsetGenerator.generate(timeWrapperProvider.get());
    	if (timeWrapper == null)
    		return null;
    	long timeOffsetGeneration = timeWrapper.unwrap();
		return new Date(dateGeneration.getTime() + timeOffsetGeneration);
	}

}

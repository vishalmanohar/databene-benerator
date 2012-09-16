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

package org.databene.platform.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.databene.commons.converter.AnyConverter;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.Entity;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.script.PrimitiveType;

/**
 * Converts a SQL {@link ResultSet} to a Benerator {@link Entity}.<br/><br/>
 * Created: 24.08.2010 12:29:56
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class ResultSet2EntityConverter {

	public static Entity convert(ResultSet resultSet, ComplexTypeDescriptor descriptor) throws SQLException {
	    Entity entity = new Entity(descriptor);
	    ResultSetMetaData metaData = resultSet.getMetaData();
	    int columnCount = metaData.getColumnCount();
	    for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
	        String columnName = metaData.getColumnName(columnIndex);
	        String typeName = null;
	        if (descriptor != null) {
	            ComponentDescriptor component = descriptor.getComponent(columnName);
	            if (component != null) {
	                SimpleTypeDescriptor type = (SimpleTypeDescriptor) component.getTypeDescriptor();
	                PrimitiveType primitiveType = type.getPrimitiveType();
	                typeName = (primitiveType != null ? primitiveType.getName() : "string");
	            } else
	                typeName = "string";
	        } else
	            typeName = "string";
	        DataModel dataModel = (descriptor != null ? descriptor.getDataModel() : null);
	        Object javaValue = javaValue(resultSet, columnIndex, typeName, dataModel);
	        entity.setComponent(columnName, javaValue);
	    }
	    return entity;
    }

    // TODO v1.0 perf: use a dedicated converter for each column
    private static Object javaValue(ResultSet resultSet, int columnIndex, String primitiveType, DataModel dataModel) throws SQLException {
        if ("date".equals(primitiveType))
            return resultSet.getDate(columnIndex);
        else if ("timestamp".equals(primitiveType))
            return resultSet.getTimestamp(columnIndex);
        else if ("string".equals(primitiveType))
            return resultSet.getString(columnIndex);
        // try generic conversion
        Object driverValue = resultSet.getObject(columnIndex);
        Object javaValue = driverValue;
        if (dataModel != null) {
	        Class<?> javaType = dataModel.getBeanDescriptorProvider().concreteType(primitiveType);
	        javaValue = AnyConverter.convert(driverValue, javaType);
        }
        return javaValue;
    }

}

/**
 * Copyright 2009-2018 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.executor.keygen;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.ArrayUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.defaults.DefaultSqlSession.StrictMap;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * Jdbc3KeyGenerator 主键生成器，只处理后置生成
 * 该主键模式，主要是返回数据库执行操作后获取到的返回值
 *
 * @author Clinton Begin
 * @author Kazuki Shimizu
 */
public class Jdbc3KeyGenerator implements KeyGenerator {

    /**
     * A shared instance.
     *
     * @since 3.4.3
     */
    public static final Jdbc3KeyGenerator INSTANCE = new Jdbc3KeyGenerator();

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        // do nothing
    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        processBatch(ms, stmt, parameter);
    }

    public void processBatch(MappedStatement ms, Statement stmt, Object parameter) {
        final String[] keyProperties = ms.getKeyProperties();
        if (keyProperties == null || keyProperties.length == 0) {
            return;
        }
        ResultSet rs = null;
        try {
            // 获取statement 执行后返回的主键信息
            rs = stmt.getGeneratedKeys();
            final Configuration configuration = ms.getConfiguration();
            if (rs.getMetaData().getColumnCount() >= keyProperties.length) {
                // 获取唯一参数
                Object soleParam = getSoleParameter(parameter);
                if (soleParam != null) {
                    assignKeysToParam(configuration, rs, keyProperties, soleParam);
                } else {
                    assignKeysToOneOfParams(configuration, rs, keyProperties, (Map<?, ?>) parameter);
                }
            }
        } catch (Exception e) {
            throw new ExecutorException("Error getting generated key or setting result to parameter object. Cause: " + e, e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    protected void assignKeysToOneOfParams(final Configuration configuration, ResultSet rs, final String[] keyProperties,
                                           Map<?, ?> paramMap) throws SQLException {
        // Assuming 'keyProperty' includes the parameter name. e.g. 'param.id'.
        int firstDot = keyProperties[0].indexOf('.');
        if (firstDot == -1) {
            throw new ExecutorException(
                    "Could not determine which parameter to assign generated keys to. "
                            + "Note that when there are multiple parameters, 'keyProperty' must include the parameter name (e.g. 'param.id'). "
                            + "Specified key properties are " + ArrayUtil.toString(keyProperties) + " and available parameters are "
                            + paramMap.keySet());
        }
        String paramName = keyProperties[0].substring(0, firstDot);
        Object param;
        if (paramMap.containsKey(paramName)) {
            param = paramMap.get(paramName);
        } else {
            throw new ExecutorException("Could not find parameter '" + paramName + "'. "
                    + "Note that when there are multiple parameters, 'keyProperty' must include the parameter name (e.g. 'param.id'). "
                    + "Specified key properties are " + ArrayUtil.toString(keyProperties) + " and available parameters are "
                    + paramMap.keySet());
        }
        // Remove param name from 'keyProperty' string. e.g. 'param.id' -> 'id'
        String[] modifiedKeyProperties = new String[keyProperties.length];
        for (int i = 0; i < keyProperties.length; i++) {
            if (keyProperties[i].charAt(firstDot) == '.' && keyProperties[i].startsWith(paramName)) {
                modifiedKeyProperties[i] = keyProperties[i].substring(firstDot + 1);
            } else {
                throw new ExecutorException("Assigning generated keys to multiple parameters is not supported. "
                        + "Note that when there are multiple parameters, 'keyProperty' must include the parameter name (e.g. 'param.id'). "
                        + "Specified key properties are " + ArrayUtil.toString(keyProperties) + " and available parameters are "
                        + paramMap.keySet());
            }
        }
        assignKeysToParam(configuration, rs, modifiedKeyProperties, param);
    }

    //将多个主键包装成一个集合类型
    private void assignKeysToParam(final Configuration configuration, ResultSet rs, final String[] keyProperties,
                                   Object param)
            throws SQLException {
        final TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        // 获取resultset元数据信息
        final ResultSetMetaData rsmd = rs.getMetaData();
        // Wrap the parameter in Collection to normalize the logic.

        // 包装执行参数为一个集合类型
        Collection<?> paramAsCollection = null;
        if (param instanceof Object[]) {
            paramAsCollection = Arrays.asList((Object[]) param);
        } else if (!(param instanceof Collection)) {
            paramAsCollection = Arrays.asList(param);
        } else {
            paramAsCollection = (Collection<?>) param;
        }
        TypeHandler<?>[] typeHandlers = null;
        for (Object obj : paramAsCollection) {
            if (!rs.next()) {
                break;
            }
            MetaObject metaParam = configuration.newMetaObject(obj);
            if (typeHandlers == null) {
                typeHandlers = getTypeHandlers(typeHandlerRegistry, metaParam, keyProperties, rsmd);
            }
            populateKeys(rs, metaParam, keyProperties, typeHandlers);
        }
    }

    // 获取唯一参数
    private Object getSoleParameter(Object parameter) {
        if (!(parameter instanceof ParamMap || parameter instanceof StrictMap)) {
            return parameter;
        }
        Object soleParam = null;
        for (Object paramValue : ((Map<?, ?>) parameter).values()) {
            if (soleParam == null) {
                soleParam = paramValue;
            } else if (soleParam != paramValue) {
                soleParam = null;
                break;
            }
        }
        return soleParam;
    }

    // 获取返回主键对应的TypeHandler，方便jdbc类型与java类型关联映射
    private TypeHandler<?>[] getTypeHandlers(TypeHandlerRegistry typeHandlerRegistry, MetaObject metaParam, String[] keyProperties, ResultSetMetaData rsmd) throws SQLException {
        TypeHandler<?>[] typeHandlers = new TypeHandler<?>[keyProperties.length];
        for (int i = 0; i < keyProperties.length; i++) {
            if (metaParam.hasSetter(keyProperties[i])) {
                Class<?> keyPropertyType = metaParam.getSetterType(keyProperties[i]);
                typeHandlers[i] = typeHandlerRegistry.getTypeHandler(keyPropertyType, JdbcType.forCode(rsmd.getColumnType(i + 1)));
            } else {
                throw new ExecutorException("No setter found for the keyProperty '" + keyProperties[i] + "' in '"
                        + metaParam.getOriginalObject().getClass().getName() + "'.");
            }
        }
        return typeHandlers;
    }

    // 填充key属性的值， TypeHandler 与keyProperties的顺序一一对应
    private void populateKeys(ResultSet rs, MetaObject metaParam, String[] keyProperties, TypeHandler<?>[] typeHandlers) throws SQLException {
        for (int i = 0; i < keyProperties.length; i++) {
            String property = keyProperties[i];
            TypeHandler<?> th = typeHandlers[i];
            if (th != null) {
                Object value = th.getResult(rs, i + 1);
                metaParam.setValue(property, value);
            }
        }
    }

}

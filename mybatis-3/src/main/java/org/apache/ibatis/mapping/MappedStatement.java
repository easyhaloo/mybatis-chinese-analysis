/**
 *    Copyright 2009-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

/**
 * 语句映射
 *
 * @author Clinton Begin
 */
public final class MappedStatement {

    private String resource;
    private Configuration configuration;
    // 执行的SQL语句唯一的ID
    private String id;
    //获取是数量
    private Integer fetchSize;
    // 超时时间
    private Integer timeout;
    // 执行的statementType类型
    private StatementType statementType;
    // 结果集类型
    private ResultSetType resultSetType;
    // SQL语句源，提取出预编译SQL
    private SqlSource sqlSource;
    // 缓存对象，将取出的结果集存放到Cahce中
    private Cache cache;
    // 参数集合,执行SQL的参数
    private ParameterMap parameterMap;
    //结果集对象，封装List，意味着将返回多条记录行
    private List<ResultMap> resultMaps;
    // 是否刷新缓存,每次执行SQL语句都会导致本地缓存和二级缓存都会被清空，默认值：false。
    private boolean flushCacheRequired;
    // 是否使用缓存，将会导致本条语句的结果被二级缓存，默认值：对 select 元素为 true。
    private boolean useCache;
    // 这个设置仅针对嵌套结果 select 语句适用：如果为 true，就是假设包含了嵌套结果集或是分组了，这样的话当返回一个主结果行的时候，就不会发生有对前面结果集的引用的情况。
    // 这就使得在获取嵌套的结果集的时候不至于导致内存不够用。默认值：false。
    private boolean resultOrdered;
    // sql操作命令
    private SqlCommandType sqlCommandType;
    // 主键类型，方便Insert操作时，到底是使用自增主键还是自定义主键
    private KeyGenerator keyGenerator;
    //selectKey 语句结果应该被设置的目标属性。如果希望得到多个生成的列，也可以是逗号分隔的属性名称列表。
    private String[] keyProperties;
    //匹配属性的返回结果集中的列名称。如果希望得到多个生成的列，也可以是逗号分隔的属性名称列表。
    private String[] keyColumns;
    // 是否存在嵌套的结果集
    private boolean hasNestedResultMaps;
    // 数据源厂商提供的名称，如果配置了 databaseIdProvider，MyBatis 会加载所有的不带 databaseId 或匹配当前 databaseId 的语句；如果带或者不带的语句都有，则不带的会被忽略。
    private String databaseId;
    // 执行SQL的日志
    private Log statementLog;
    // 解析XML中的动态SQL，将实际参数传递给SQL编译成预编译模式的SQL,可以自定义解析过程，须实现LanguageDriver接口
    private LanguageDriver lang;
    //这个设置仅对多结果集的情况适用，它将列出语句执行后返回的结果集并每个结果集给一个名称，名称是逗号分隔的。
    private String[] resultSets;

    MappedStatement() {
        // constructor disabled
    }

    public static class Builder {
        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration, String id, SqlSource sqlSource, SqlCommandType sqlCommandType) {
            mappedStatement.configuration = configuration;
            mappedStatement.id = id;
            mappedStatement.sqlSource = sqlSource;
            mappedStatement.statementType = StatementType.PREPARED;
            mappedStatement.resultSetType = ResultSetType.DEFAULT;
            mappedStatement.parameterMap = new ParameterMap.Builder(configuration, "defaultParameterMap", null, new ArrayList<>()).build();
            mappedStatement.resultMaps = new ArrayList<>();
            mappedStatement.sqlCommandType = sqlCommandType;
            mappedStatement.keyGenerator = configuration.isUseGeneratedKeys() && SqlCommandType.INSERT.equals(sqlCommandType) ? Jdbc3KeyGenerator.INSTANCE : NoKeyGenerator.INSTANCE;
            String logId = id;
            if (configuration.getLogPrefix() != null) {
                logId = configuration.getLogPrefix() + id;
            }
            mappedStatement.statementLog = LogFactory.getLog(logId);
            mappedStatement.lang = configuration.getDefaultScriptingLanguageInstance();
        }

        public Builder resource(String resource) {
            mappedStatement.resource = resource;
            return this;
        }

        public String id() {
            return mappedStatement.id;
        }

        public Builder parameterMap(ParameterMap parameterMap) {
            mappedStatement.parameterMap = parameterMap;
            return this;
        }

        public Builder resultMaps(List<ResultMap> resultMaps) {
            mappedStatement.resultMaps = resultMaps;
            for (ResultMap resultMap : resultMaps) {
                mappedStatement.hasNestedResultMaps = mappedStatement.hasNestedResultMaps || resultMap.hasNestedResultMaps();
            }
            return this;
        }

        public Builder fetchSize(Integer fetchSize) {
            mappedStatement.fetchSize = fetchSize;
            return this;
        }

        public Builder timeout(Integer timeout) {
            mappedStatement.timeout = timeout;
            return this;
        }

        public Builder statementType(StatementType statementType) {
            mappedStatement.statementType = statementType;
            return this;
        }

        public Builder resultSetType(ResultSetType resultSetType) {
            mappedStatement.resultSetType = resultSetType == null ? ResultSetType.DEFAULT : resultSetType;
            return this;
        }

        public Builder cache(Cache cache) {
            mappedStatement.cache = cache;
            return this;
        }

        public Builder flushCacheRequired(boolean flushCacheRequired) {
            mappedStatement.flushCacheRequired = flushCacheRequired;
            return this;
        }

        public Builder useCache(boolean useCache) {
            mappedStatement.useCache = useCache;
            return this;
        }

        public Builder resultOrdered(boolean resultOrdered) {
            mappedStatement.resultOrdered = resultOrdered;
            return this;
        }

        public Builder keyGenerator(KeyGenerator keyGenerator) {
            mappedStatement.keyGenerator = keyGenerator;
            return this;
        }

        public Builder keyProperty(String keyProperty) {
            mappedStatement.keyProperties = delimitedStringToArray(keyProperty);
            return this;
        }

        public Builder keyColumn(String keyColumn) {
            mappedStatement.keyColumns = delimitedStringToArray(keyColumn);
            return this;
        }

        public Builder databaseId(String databaseId) {
            mappedStatement.databaseId = databaseId;
            return this;
        }

        public Builder lang(LanguageDriver driver) {
            mappedStatement.lang = driver;
            return this;
        }

        public Builder resultSets(String resultSet) {
            mappedStatement.resultSets = delimitedStringToArray(resultSet);
            return this;
        }

        /**
         * @deprecated Use {@link #resultSets}
         */
        @Deprecated
        public Builder resulSets(String resultSet) {
            mappedStatement.resultSets = delimitedStringToArray(resultSet);
            return this;
        }

        public MappedStatement build() {
            assert mappedStatement.configuration != null;
            assert mappedStatement.id != null;
            assert mappedStatement.sqlSource != null;
            assert mappedStatement.lang != null;
            mappedStatement.resultMaps = Collections.unmodifiableList(mappedStatement.resultMaps);
            return mappedStatement;
        }
    }

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public String getResource() {
        return resource;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getId() {
        return id;
    }

    public boolean hasNestedResultMaps() {
        return hasNestedResultMaps;
    }

    public Integer getFetchSize() {
        return fetchSize;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public StatementType getStatementType() {
        return statementType;
    }

    public ResultSetType getResultSetType() {
        return resultSetType;
    }

    public SqlSource getSqlSource() {
        return sqlSource;
    }

    public ParameterMap getParameterMap() {
        return parameterMap;
    }

    public List<ResultMap> getResultMaps() {
        return resultMaps;
    }

    public Cache getCache() {
        return cache;
    }

    public boolean isFlushCacheRequired() {
        return flushCacheRequired;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public boolean isResultOrdered() {
        return resultOrdered;
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public String[] getKeyProperties() {
        return keyProperties;
    }

    public String[] getKeyColumns() {
        return keyColumns;
    }

    public Log getStatementLog() {
        return statementLog;
    }

    public LanguageDriver getLang() {
        return lang;
    }

    public String[] getResultSets() {
        return resultSets;
    }

    /**
     * @deprecated Use {@link #getResultSets()}
     */
    @Deprecated
    public String[] getResulSets() {
        return resultSets;
    }

    /**
     * 获取BoundSql 这个对象，该对象中存在预处理的SQL语句，以及要执行的SQL参数
     * @param parameterObject
     * @return
     */
    public BoundSql getBoundSql(Object parameterObject) {
        BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings == null || parameterMappings.isEmpty()) {
            boundSql = new BoundSql(configuration, boundSql.getSql(), parameterMap.getParameterMappings(), parameterObject);
        }

        // 检查参数映射中的嵌套结果映射
        // check for nested result maps in parameter mappings (issue #30)
        for (ParameterMapping pm : boundSql.getParameterMappings()) {
            String rmId = pm.getResultMapId();
            if (rmId != null) {
                ResultMap rm = configuration.getResultMap(rmId);
                if (rm != null) {
                    hasNestedResultMaps |= rm.hasNestedResultMaps();
                }
            }
        }

        return boundSql;
    }

    private static String[] delimitedStringToArray(String in) {
        if (in == null || in.trim().length() == 0) {
            return null;
        } else {
            return in.split(",");
        }
    }

}

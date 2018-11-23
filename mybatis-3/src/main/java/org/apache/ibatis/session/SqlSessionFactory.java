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
package org.apache.ibatis.session;

import java.sql.Connection;

/**
 * 从connection或DataSource创建{@link SqlSession}
 *  默认从DataSource中创建SqlSession
 * Creates an {@link SqlSession} out of a connection or a DataSource
 *
 * @author Clinton Begin
 */
public interface SqlSessionFactory {
    /**
     * 默认从数据源中获取SqlSession，默认不自动提交
     *
     * @return
     */
    SqlSession openSession();

    /**
     * 默认从数据源中获取SqlSession，并设置是否可自动提交
     *
     * @return
     */
    SqlSession openSession(boolean autoCommit);

    /**
     * 通过connection来获取SqlSession
     * @param connection
     * @return
     */
    SqlSession openSession(Connection connection);

    /**
     * 指定事务等级，并返回SqlSession
     * @param level
     * @return
     */
    SqlSession openSession(TransactionIsolationLevel level);
    /**
     * 指定SQL执行方式，并返回SqlSession
     * @param execType
     * @return
     */
    SqlSession openSession(ExecutorType execType);
    /**
     * 指定SQL执行方式，以及提交方式
     * @param execType
     * @return
     */
    SqlSession openSession(ExecutorType execType, boolean autoCommit);
    /**
     * 指定SQL执行方式，以及事务等级
     * @param execType
     * @return
     */
    SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level);
    /**
     * 指定SQL执行方式，通过connection来创建SqlSession
     * @param execType
     * @return
     */
    SqlSession openSession(ExecutorType execType, Connection connection);

    Configuration getConfiguration();

}

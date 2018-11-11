/**
 *    Copyright 2009-2015 the original author or authors.
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
package org.apache.ibatis.transaction;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.session.TransactionIsolationLevel;

/**
 *
 *
 * Creates {@link Transaction} instances.
 * 使用工厂模式来创建事务的实例，主要实现有
 *  -  ManagedTransactionFactory
 *     默认忽略自动提交和隔离级别，因为托管事务完全由外部管理控制
 *     自动提交将通过外部的配置文件进行控制，默认是开启的。
 *  -  JdbcTransactionFactory
 *
 * @author Clinton Begin
 */
public interface TransactionFactory {

  /**
   * 设置自定义的配置参数
   * Sets transaction factory custom properties.
   * @param props
   */
  void setProperties(Properties props);

  /**
   * 从现有连接中获取一个事务
   * Creates a {@link Transaction} out of an existing connection.
   * @param conn Existing database connection
   * @return Transaction
   * @since 3.1.0
   */
  Transaction newTransaction(Connection conn);
  
  /** 从现有的数据源中获取一个事务
   * Creates a {@link Transaction} out of a datasource.
   * @param dataSource DataSource to take the connection from
   * @param level Desired isolation level
   * @param autoCommit Desired autocommit
   * @return Transaction
   * @since 3.1.0
   */
  Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit);

}

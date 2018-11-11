/**
 * Copyright 2009-2015 the original author or authors.
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
package org.apache.ibatis.session;

import java.sql.Connection;

/**
 * 事务的隔离级别，默认调用JDK中原生的管理能力
 * @author Clinton Begin
 */
public enum TransactionIsolationLevel {
    //表示不支持事务
    NONE(Connection.TRANSACTION_NONE),
    //不允许脏读，不能重复读取，但是可以发生幻读，此级别仅禁止事务读取具有未提交更改的行。
    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
    //表示可以发生脏读，不可重复读和幻像读。允许读取一个事物更改的行，在该行的任何更改之前承诺（“脏读”）。 如果任何更改被回滚，
    //第二个事务将检索到无效的行。
    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
    //防止脏读和重复读取，幻读可以发生。此级别仅禁止事务读取具有未提交更改的行
    //并且还禁止一个事务读取行，第二个事务更改行，第一个事务读取行，第二次获取不同值的情况（“不可重复读”）。
    REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
    //防止脏读，重复读取，幻读。
    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

    private final int level;

    private TransactionIsolationLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}

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
package org.apache.ibatis.cursor.defaults;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetWrapper;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 这是一个默认的实现，不是线程安全的
 * <p>
 * This is the default implementation of a MyBatis Cursor.
 * This implementation is not thread safe.
 *
 * @author Guillaume Darmont / guillaume@dropinocean.com
 */
public class DefaultCursor<T> implements Cursor<T> {

    // ResultSetHandler stuff
    // 默认的结果集处理器
    private final DefaultResultSetHandler resultSetHandler;
    // 结果集映射器
    private final ResultMap resultMap;
    // 结果集包装器
    private final ResultSetWrapper rsw;
    // 分页绑定器
    private final RowBounds rowBounds;
    // 对象包装结果处理器
    private final ObjectWrapperResultHandler<T> objectWrapperResultHandler = new ObjectWrapperResultHandler<>();
    //游标迭代器
    private final CursorIterator cursorIterator = new CursorIterator();
    // 是否进行迭代器检索
    private boolean iteratorRetrieved;
    // 游标状态
    private CursorStatus status = CursorStatus.CREATED;
    private int indexWithRowBound = -1;

    /**
     * 游标状态枚举类
     */
    private enum CursorStatus {
        /**
         * 新创建的游标，数据库ResultSet消耗尚未开始
         * <p>
         * A freshly created cursor, database ResultSet consuming has not started
         */
        CREATED,
        /**
         * 当前正在使用的游标,数据库ResultSet消耗尚开始
         * A cursor currently in use, database ResultSet consuming has started
         */
        OPEN,
        /**
         * 游标关闭，没有完全消耗
         * A closed cursor, not fully consumed
         */
        CLOSED,
        /**
         * 完全消耗的游标，消耗的游标总是关闭
         * A fully consumed cursor, a consumed cursor is always closed
         */
        CONSUMED
    }

    public DefaultCursor(DefaultResultSetHandler resultSetHandler, ResultMap resultMap, ResultSetWrapper rsw, RowBounds rowBounds) {
        this.resultSetHandler = resultSetHandler;
        this.resultMap = resultMap;
        this.rsw = rsw;
        this.rowBounds = rowBounds;
    }

    @Override
    public boolean isOpen() {
        return status == CursorStatus.OPEN;
    }

    @Override
    public boolean isConsumed() {
        return status == CursorStatus.CONSUMED;
    }

    @Override
    public int getCurrentIndex() {
        return rowBounds.getOffset() + cursorIterator.iteratorIndex;
    }

    @Override
    public Iterator<T> iterator() {
        if (iteratorRetrieved) {
            throw new IllegalStateException("Cannot open more than one iterator on a Cursor");
        }
        if (isClosed()) {
            throw new IllegalStateException("A Cursor is already closed.");
        }
        iteratorRetrieved = true;
        return cursorIterator;
    }

    @Override
    public void close() {
        if (isClosed()) {
            return;
        }

        ResultSet rs = rsw.getResultSet();
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            // ignore
        } finally {
            status = CursorStatus.CLOSED;
        }
    }

    protected T fetchNextUsingRowBound() {
        T result = fetchNextObjectFromDatabase();
        while (result != null && indexWithRowBound < rowBounds.getOffset()) {
            result = fetchNextObjectFromDatabase();
        }
        return result;
    }

    /**
     * 从数据库中获取下一个结果集对象
     *
     * @return
     */
    protected T fetchNextObjectFromDatabase() {
        if (isClosed()) {
            return null;
        }

        try {
            status = CursorStatus.OPEN;
            if (!rsw.getResultSet().isClosed()) {
                resultSetHandler.handleRowValues(rsw, resultMap, objectWrapperResultHandler, RowBounds.DEFAULT, null);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        T next = objectWrapperResultHandler.result;
        if (next != null) {
            indexWithRowBound++;
        }
        // 没有更多对象或者消费达到了极限，则将游标状态置为CONSUMED状态
        // No more object or limit reached
        if (next == null || getReadItemsCount() == rowBounds.getOffset() + rowBounds.getLimit()) {
            close();
            status = CursorStatus.CONSUMED;
        }
        objectWrapperResultHandler.result = null;

        return next;
    }

    private boolean isClosed() {
        return status == CursorStatus.CLOSED || status == CursorStatus.CONSUMED;
    }

    private int getReadItemsCount() {
        return indexWithRowBound + 1;
    }

    private static class ObjectWrapperResultHandler<T> implements ResultHandler<T> {

        private T result;

        @Override
        public void handleResult(ResultContext<? extends T> context) {
            this.result = context.getResultObject();
            context.stop();
        }
    }

    private class CursorIterator implements Iterator<T> {

        /**
         * Holder for the next object to be returned
         */
        T object;

        /**
         * Index of objects returned using next(), and as such, visible to users.
         */
        int iteratorIndex = -1;

        @Override
        public boolean hasNext() {
            if (object == null) {
                object = fetchNextUsingRowBound();
            }
            return object != null;
        }

        @Override
        public T next() {
            // Fill next with object fetched from hasNext()
            T next = object;

            if (next == null) {
                next = fetchNextUsingRowBound();
            }

            if (next != null) {
                object = null;
                iteratorIndex++;
                return next;
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove element from Cursor");
        }
    }
}

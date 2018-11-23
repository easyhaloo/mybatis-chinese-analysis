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
package org.apache.ibatis.cursor;

import java.io.Closeable;

/**
 * 游标契约是使用迭代器方式懒惰的获取条目
 * 游标非常适合处理 百万级别的查询，这些查询结果不能直接存放在内存中，可以使用迭代器的模式逐个读取
 * 如果要想使用游标，必须使用resultMap的id列 加上(resultOrdered="true"),对返回结果进行排序
 * <p>
 * Cursor contract to handle fetching items lazily using an Iterator.
 * Cursors are a perfect fit to handle millions of items queries that would not normally fits in memory.
 * Cursor SQL queries must be ordered (resultOrdered="true") using the id columns of the resultMap.
 *
 * @author Guillaume Darmont / guillaume@dropinocean.com
 */
public interface Cursor<T> extends Closeable, Iterable<T> {

    /**
     * 如果游标开始从数据库获取数据，则返回true
     *
     * @return true if the cursor has started to fetch items from database.
     */
    boolean isOpen();

    /**
     * 如果光标已完全耗尽并返回了与查询匹配的所有元素
     *
     * @return true if the cursor is fully consumed and has returned all elements matching the query.
     */
    boolean isConsumed();

    /**
     * 获取当前元素的Index索引，如果是第一个下标为0
     * 为-1则没有检索到数据
     * Get the current item index. The first item has the index 0.
     *
     * @return -1 if the first cursor item has not been retrieved. The index of the current item retrieved.
     */
    int getCurrentIndex();
}

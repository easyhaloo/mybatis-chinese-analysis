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
package org.apache.ibatis.executor;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Array;
import java.util.List;

/**
 *  结果集抽取器
 * @author Andrew Gustafson
 */
public class ResultExtractor {
    // 配置对象
    private final Configuration configuration;
    // 对象工厂，实例化对象
    private final ObjectFactory objectFactory;

    public ResultExtractor(Configuration configuration, ObjectFactory objectFactory) {
        this.configuration = configuration;
        this.objectFactory = objectFactory;
    }

    /**
     *  将list转化成targetType类型的集合
     *    集合类型分为 List,数组,以及Collection
     *
     *  当targetType不是集合的时候，返回list的第一个元素
     * @param list
     * @param targetType
     * @return
     */
    public Object extractObjectFromList(List<Object> list, Class<?> targetType) {
        Object value = null;


        // targetType 是list的父类，或者接口，或者targetType与List是同一个类或者接口
        if (targetType != null && targetType.isAssignableFrom(list.getClass())) {
            value = list;
        } else if (targetType != null && objectFactory.isCollection(targetType)) {
            value = objectFactory.create(targetType);
            MetaObject metaObject = configuration.newMetaObject(value);
            metaObject.addAll(list);


        } else if (targetType != null && targetType.isArray()) {
            // 数组类型
            Class<?> arrayComponentType = targetType.getComponentType();
            Object array = Array.newInstance(arrayComponentType, list.size());
            // 原生类型，直接使用数组
            if (arrayComponentType.isPrimitive()) {
                for (int i = 0; i < list.size(); i++) {
                    Array.set(array, i, list.get(i));
                }
                value = array;
            } else {
                // 其他类型的集合
                value = list.toArray((Object[]) array);
            }
        } else {
            if (list != null && list.size() > 1) {
                throw new ExecutorException("Statement returned more than one row, where no more than one was expected.");
            } else if (list != null && list.size() == 1) {
                value = list.get(0);
            }
        }
        return value;
    }
}

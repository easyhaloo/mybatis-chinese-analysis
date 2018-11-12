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
package org.apache.ibatis.reflection.wrapper;

import java.util.List;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;

/**
 * @author Clinton Begin
 */
public interface ObjectWrapper {
    /**
     * 利用Invoker反射来获取`PropertyTokenizer`顶部属性值
     *
     * @param prop
     * @return
     */
    Object get(PropertyTokenizer prop);

    /**
     * 利用Invoker反射来设置`PropertyTokenizer`顶部属性值
     *
     * @param prop
     * @return
     */
    void set(PropertyTokenizer prop, Object value);

    /**
     * 改造name属性，对于可以使用反射执行的方法，重新组装成 链式的属性
     * <p>
     * person.student.name  如果student不能使用反射获取，那么person.student.name = > person
     *
     * @param name
     * @param useCamelCaseMapping
     * @return
     */
    String findProperty(String name, boolean useCamelCaseMapping);

    /**
     * 获取所有可以执行的setter,getter方法
     *
     * @return
     */
    String[] getGetterNames();

    String[] getSetterNames();

    /**
     * 获取所有可以执行的setter,getter方法参数类型，或者返回值类型
     *
     * @return
     */
    Class<?> getSetterType(String name);

    Class<?> getGetterType(String name);

    /**
     * 在属性链中寻找setter,getter方法，一旦中间链不存在setter,getter方法直接返回false
     *
     * @param name
     * @return
     */
    boolean hasSetter(String name);

    boolean hasGetter(String name);

    /**
     * 初始化属性对象实例
     * @param name
     * @param prop
     * @param objectFactory
     * @return
     */
    MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory);

    boolean isCollection();

    /**
     * 仅提供CollectionWrapper使用，基于原生的集合类使用
     *
     * @param element
     */
    void add(Object element);

    /**
     * 仅提供CollectionWrapper使用，基于原生的集合类使用
     *
     * @param element
     */
    <E> void addAll(List<E> element);

}

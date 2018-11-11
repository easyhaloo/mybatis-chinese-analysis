/**
 *    Copyright 2009-2016 the original author or authors.
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
package org.apache.ibatis.plugin;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 一般与@Intercepts注解联合使用
 * @Signature 是一个大的签名对象，内部主要存放反射时需要用到的一些信息，比如，反射的类型，反射类型中的方法，以及调用方法所需要的参数类型
 *
 * @author Clinton Begin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Signature {
  //反射的类型
  //一般来说，Mybatis只会拦截 StatementHandler 、 Executor、MappedStatement、ResultSetHandler
  Class<?> type();
  //类型中的方法
  String method();
  //调用方法的参数类型，避免重载方法
  Class<?>[] args();
}
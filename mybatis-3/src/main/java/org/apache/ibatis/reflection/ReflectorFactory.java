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
package org.apache.ibatis.reflection;

/**
 * 反射工具工厂类
 */
public interface ReflectorFactory {
  //是否开启Class缓存，开启缓存后，调用findForClass方法，会直接从缓存中获取。
  //内部维持的是ConcurrentMap
  boolean isClassCacheEnabled();

  void setClassCacheEnabled(boolean classCacheEnabled);
  //如果开启缓存，对与不再缓存中的type,会先创建，然后再放入缓存中。
  Reflector findForClass(Class<?> type);
}
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
package org.apache.ibatis.session;

/**
 * 指定MyBatis是否以及如何自动将列映射到fields / properties。
 * Specifies if and how MyBatis should automatically map columns to fields/properties.
 * 
 * @author Eduardo Macarron
 */
public enum AutoMappingBehavior {

  /** 不进行自动映射
   * Disables auto-mapping.
   */
  NONE,

  /**
   * 半自动，不支持内部嵌套属性的映射，也就是说对象内部含有对象属性则不支持映射（这个是默认使用的）
   * Will only auto-map results with no nested result mappings defined inside.
   */
  PARTIAL,

  /**
   * 全自动，即使是内部嵌套的对象属性也可以支持映射
   * Will auto-map result mappings of any complexity (containing nested or otherwise).
   */
  FULL
}

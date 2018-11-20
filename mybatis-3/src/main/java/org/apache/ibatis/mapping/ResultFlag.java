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
package org.apache.ibatis.mapping;

/**
 *  结果映射类型
 * @author Clinton Begin
 */
public enum ResultFlag {
    // id   一个 ID 结果;标记出作为 ID 的结果可以帮助提高整体性能
    // CONSTRUCTOR 用于在实例化类时，注入结果到构造方法中
    // idArg - ID 参数;标记出作为 ID 的结果可以帮助提高整体性能
    //  arg - 将被注入到构造方法的一个普通结果
    ID, CONSTRUCTOR
}

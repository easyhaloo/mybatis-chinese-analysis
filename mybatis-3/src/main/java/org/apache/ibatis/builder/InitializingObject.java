/**
 * Copyright 2009-2016 the original author or authors.
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
package org.apache.ibatis.builder;

/**
 * 该接表示提供初始化方法的接口
 * Interface that indicate to provide a initialization method.
 *
 * @author Kazuki Shimizu
 * @since 3.4.2
 */
public interface InitializingObject {

    /**
     * 初始化实例
     *
     * 在设置了所有属性后，将调用此方法。
     *
     * 如果配置错误（例如未能设置基本属性）或初始化失败，将会抛出异常
     * Initialize a instance.
     * <p>
     * This method will be invoked after it has set all properties.
     * </p>
     *
     * @throws Exception in the event of misconfiguration (such as failure to set an essential property) or if initialization fails
     */
    void initialize() throws Exception;

}
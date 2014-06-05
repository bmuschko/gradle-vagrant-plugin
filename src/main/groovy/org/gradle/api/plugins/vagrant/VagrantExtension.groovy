/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.plugins.vagrant

import org.gradle.util.ConfigureUtil

class VagrantExtension {
    /**
     * The directory of the Vagrant box.
     */
    File boxDir

    /**
     * The backend provider.
     */
    String provider

    /**
     * The environment variables passed to Vagrant.
     */
    EnvironmentVariables environmentVariables = new EnvironmentVariables()

    /**
     * Installation variables.
     */
    Installation installation = new Installation()

    void environmentVariables(Closure closure) {
        ConfigureUtil.configure(closure, environmentVariables)
    }

    void installation(Closure closure) {
        ConfigureUtil.configure(closure, installation)
    }
}

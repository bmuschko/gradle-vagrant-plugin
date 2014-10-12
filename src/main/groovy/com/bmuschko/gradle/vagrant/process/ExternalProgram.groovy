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
package com.bmuschko.gradle.vagrant.process

import com.bmuschko.gradle.vagrant.utils.OsUtils

enum ExternalProgram {
    VIRTUALBOX('VirtualBox', 'vboxmanage'), VAGRANT('Vagrant', 'vagrant')

    private final String name
    private final String executable

    ExternalProgram(String name, String executable) {
        this.name = name
        this.executable = executable
    }

    String getName() {
        name
    }

    String getExecutable() {
        executable
    }

    List<String> getCommandLineArgs() {
        def commandLineArgs = []

        if(OsUtils.isOSWindows()) {
            commandLineArgs << 'cmd'
            commandLineArgs << '/c'
        }

        commandLineArgs << executable
        commandLineArgs
    }
}

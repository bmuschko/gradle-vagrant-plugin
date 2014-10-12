/*
 * Copyright 2014 the original author or authors.
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

import spock.lang.Specification

class ExternalProgramSpec extends Specification {
    static final String OS_NAME_SYSTEM_PROPERTY = 'os.name'
    String osName

    def setup() {
        osName = System.properties[OS_NAME_SYSTEM_PROPERTY]
    }

    def cleanup() {
        System.properties[OS_NAME_SYSTEM_PROPERTY] = osName
    }

    def "Get name for external program"() {
        expect:
            ExternalProgram.VAGRANT.name == 'Vagrant'
            ExternalProgram.VIRTUALBOX.name == 'VirtualBox'
    }

    def "Get executable for external program"() {
        expect:
            ExternalProgram.VAGRANT.executable == 'vagrant'
            ExternalProgram.VIRTUALBOX.executable == 'vboxmanage'
    }

    def "Get executable and command line args for Linux"() {
        when:
        System.properties[OS_NAME_SYSTEM_PROPERTY] = 'Linux'

        then:
        ExternalProgram.VAGRANT.commandLineArgs == [ExternalProgram.VAGRANT.executable]
        ExternalProgram.VIRTUALBOX.commandLineArgs == [ExternalProgram.VIRTUALBOX.executable]
    }

    def "Get executable and command line args for Windows"() {
        when:
        System.properties[OS_NAME_SYSTEM_PROPERTY] = 'Windows'

        then:
        ExternalProgram.VAGRANT.commandLineArgs == ['cmd', '/c', ExternalProgram.VAGRANT.executable]
        ExternalProgram.VIRTUALBOX.commandLineArgs == ['cmd', '/c', ExternalProgram.VIRTUALBOX.executable]
    }
}

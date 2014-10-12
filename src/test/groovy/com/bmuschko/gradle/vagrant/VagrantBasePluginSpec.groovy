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
package com.bmuschko.gradle.vagrant

import com.bmuschko.gradle.vagrant.tasks.Vagrant
import com.bmuschko.gradle.vagrant.tasks.VagrantDestroy
import com.bmuschko.gradle.vagrant.tasks.VagrantUp
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.Unroll

class VagrantBasePluginSpec extends Specification {
    Project project

    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'com.bmuschko.vagrant-base'
    }

    def "Box directory defaults to project directory if not set"() {
        when:
            def task = project.task('myCustomVagrantUp', type: VagrantUp)
        then:
            task.boxDir == project.projectDir
    }

    def "Box directory is set to value from extension"() {
        when:
            project.vagrant {
                boxDir = project.file('someDir')
            }

            def task = project.task('myCustomVagrantUp', type: VagrantUp)
        then:
            task.boxDir == project.file('someDir')
    }

    def "Box directory is set as property value"() {
        when:
            project.ext.boxDir = project.file('other')
            def task = project.task('myCustomVagrantUp', type: VagrantUp)
        then:
            task.boxDir == project.file('other')
    }

    def "Provider defaults to VirtualBox if not set"() {
        when:
            def task = project.task('myCustomVagrantUp', type: VagrantUp)
        then:
            task.provider == Provider.VIRTUALBOX.name
    }

    def "Provider is set to value from extension"() {
        when:
            project.vagrant {
                provider = 'vmware_fusion'
            }

            def task = project.task('myCustomVagrantUp', type: VagrantUp)
        then:
            task.provider == 'vmware_fusion'
    }

    def "Provider is set as property value"() {
        when:
            project.ext.provider = 'vmware_fusion'
            def task = project.task('myCustomVagrantUp', type: VagrantUp)
        then:
            task.provider == 'vmware_fusion'
    }

    @Unroll
    def "Installation validation is set to #enabledValidation value from extension via exposed method"() {
        when:
            project.vagrant {
                installation {
                    validate enabledValidation
                }
            }
        then:
            project.extensions.findByName('vagrant').installation.validate == enabledValidation

        where:
            enabledValidation << [true, false]
    }

    @Unroll
    def "Installation validation is set to #enabledValidation value from extension via setter method"() {
        when:
        project.vagrant {
            installation {
                validate = enabledValidation
            }
        }
        then:
        project.extensions.findByName('vagrant').installation.validate == enabledValidation

        where:
        enabledValidation << [true, false]
    }

    def "Installation validation defaults to enabled if not set"() {
        expect:
            project.extensions.findByName('vagrant').installation.validate
    }

    def "Can create task of type Vagrant with default values"() {
        when:
            def task = project.task('vagrantListsBoxes', type: Vagrant) {
                description = 'Outputs a list of available Vagrant boxes.'
                commands = ['box', 'list']
            }
        then:
            project.tasks.findByName('vagrantListsBoxes')
            task.description == 'Outputs a list of available Vagrant boxes.'
            task.commands == ['box', 'list']
            task.boxDir == project.projectDir
    }

    def "Can create task of type Vagrant with custom values"() {
        when:
            def task = project.task('myCustomVagrantUp', type: VagrantUp) {
                description = 'Brings up Vagrant box.'
                boxDir = project.file('custom')
                provider = 'vmware_fusion'
            }
        then:
            project.tasks.findByName('myCustomVagrantUp')
            task.description == 'Brings up Vagrant box.'
            task.commands == ['up']
            task.boxDir == project.file('custom')
            task.provider == 'vmware_fusion'
    }

    def "Can create multiple tasks of type Vagrant with custom values"() {
        when:
            project.ext.customBoxDir = project.file('custom')
            project.ext.fusionProvider = 'vmware_fusion'

            def upTask = project.task('fusionBoxUp', type: VagrantUp) {
                description = 'Brings up Fusion Vagrant box.'
                boxDir = project.customBoxDir
                provider = project.fusionProvider
            }

            def destroyTask = project.task('fusionBoxDestroy', type: VagrantDestroy) {
                description = 'Destroys Fusion Vagrant box.'
                boxDir = project.customBoxDir
            }
        then:
            project.tasks.findByName('fusionBoxUp')
            upTask.description == 'Brings up Fusion Vagrant box.'
            upTask.commands == ['up']
            upTask.boxDir == project.file('custom')
            upTask.provider == 'vmware_fusion'
            project.tasks.findByName('fusionBoxDestroy')
            destroyTask.description == 'Destroys Fusion Vagrant box.'
            destroyTask.commands == ['destroy', '--force']
            destroyTask.boxDir == project.file('custom')
    }
}

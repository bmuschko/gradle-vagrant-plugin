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
package com.bmuschko.gradle.vagrant.tasks

import com.bmuschko.gradle.vagrant.process.ExternalProcessExecutionResult
import com.bmuschko.gradle.vagrant.process.ExternalProcessExecutor
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class VagrantUpSpec extends Specification {
    static final TASK_NAME = 'someVagrantTask'
    Project project
    ExternalProcessExecutor mockExternalProcessExecutor

    def setup() {
        project = ProjectBuilder.builder().build()
        mockExternalProcessExecutor = Mock()
    }

    def "Executes task for thrown exception"() {
        expect:
            ExternalProcessExecutionResult result = new ExternalProcessExecutionResult(exitValue: 1, text: 'failure')
        when:
            Task task = project.task(TASK_NAME, type: VagrantUp) {
                commands = ['up']
                boxDir = project.file('mybox')
                provider = 'vmware_fusion'
            }

            task.processExecutor = mockExternalProcessExecutor
            task.runCommand()
        then:
            project.tasks.findByName(TASK_NAME) != null
            project.tasks.findByName(TASK_NAME).group == Vagrant.TASK_GROUP
            1 * mockExternalProcessExecutor.execute(['vagrant', 'up', '--provider=vmware_fusion'], null, project.file('mybox')) >> result
            !result.isOK()
            Throwable t = thrown(GradleException)
            t.message == 'Failed to execute the Vagrant command.'
    }

    def "Executes task for success with declared provider"() {
        expect:
            ExternalProcessExecutionResult result = new ExternalProcessExecutionResult(exitValue: 0, text: 'success')
        when:
            Task task = project.task(TASK_NAME, type: VagrantUp) {
                commands = ['up']
                boxDir = project.file('mybox')
                provider = 'vmware_fusion'
            }

            task.processExecutor = mockExternalProcessExecutor
            task.runCommand()
        then:
            project.tasks.findByName(TASK_NAME) != null
            project.tasks.findByName(TASK_NAME).group == Vagrant.TASK_GROUP
            1 * mockExternalProcessExecutor.execute(['vagrant', 'up', '--provider=vmware_fusion'], null, project.file('mybox')) >> result
            result.isOK()
    }

    def "Executes task for success without declared provider"() {
        expect:
            ExternalProcessExecutionResult result = new ExternalProcessExecutionResult(exitValue: 0, text: 'success')
        when:
            Task task = project.task(TASK_NAME, type: VagrantUp) {
                commands = ['up']
                boxDir = project.file('mybox')
            }

            task.processExecutor = mockExternalProcessExecutor
            task.runCommand()
        then:
            project.tasks.findByName(TASK_NAME) != null
            project.tasks.findByName(TASK_NAME).group == Vagrant.TASK_GROUP
            1 * mockExternalProcessExecutor.execute(['vagrant', 'up'], null, project.file('mybox')) >> result
            result.isOK()
    }
}

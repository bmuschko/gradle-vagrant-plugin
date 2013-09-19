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
package org.gradle.api.plugins.vagrant.tasks

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.vagrant.internal.ExternalProcessExecutionResult
import org.gradle.api.plugins.vagrant.internal.ExternalProcessExecutor
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class VagrantSpec extends Specification {
    static final TASK_NAME = 'someVagrantTask'
    Project project
    ExternalProcessExecutor mockExternalProcessExecutor

    def setup() {
        project = ProjectBuilder.builder().build()
        mockExternalProcessExecutor = Mock(ExternalProcessExecutor)
    }

    def "Executes task for thrown exception"() {
        when:
            Task task = project.task(TASK_NAME, type: Vagrant) {
                commands = ['box', 'list']
                boxDir = project.file('mybox')
            }

            task.processExecutor = mockExternalProcessExecutor
            task.runCommand()
        and:
            ExternalProcessExecutionResult result = new ExternalProcessExecutionResult(exitValue: 1, text: 'failure')
        then:
            project.tasks.findByName(TASK_NAME) != null
            project.tasks.findByName(TASK_NAME).group == Vagrant.TASK_GROUP
            1 * mockExternalProcessExecutor.execute(['vagrant', 'box', 'list'], null, project.file('mybox')) >> result
            !result.isOK()
            Exception e = thrown(GradleException)
            e.message == 'Failed to execute the Vagrant command.'
    }
}

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

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.plugins.vagrant.internal.ExternalProcessExecutionResult
import org.gradle.api.plugins.vagrant.internal.ExternalProcessExecutor
import org.gradle.api.plugins.vagrant.internal.GDKExternalProcessExecutor
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class Vagrant extends DefaultTask {
    static final String TASK_GROUP = 'Vagrant'
    static final String EXECUTABLE = 'vagrant'

    /**
     * The Vagrant command to run.
     */
    @Input
    List<String> commands

    /**
     * The directory the targeted Vagrant box resides in.
     */
    @Input
    File boxDir

    ExternalProcessExecutor processExecutor

    Vagrant() {
        group = TASK_GROUP
        processExecutor = new GDKExternalProcessExecutor(System.out, System.err)
    }

    @TaskAction
    void runCommand() {
        List<String> vagrantCommands = getCommands()
        vagrantCommands.add(0, EXECUTABLE)
        vagrantCommands.addAll(getOptions())
        logger.info "Executing Vagrant command: '${vagrantCommands.join(' ')}'"

        ExternalProcessExecutionResult result = processExecutor.execute(vagrantCommands, null, getBoxDir())

        if(!result.isOK()) {
            throw new GradleException('Failed to execute the Vagrant command.')
        }
    }

    List<String> getOptions() {
        []
    }
}

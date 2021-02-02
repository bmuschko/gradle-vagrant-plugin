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
import com.bmuschko.gradle.vagrant.process.ExternalProgram
import com.bmuschko.gradle.vagrant.process.GDKExternalProcessExecutor
import com.bmuschko.gradle.vagrant.utils.OsUtils
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

class Vagrant extends DefaultTask {
    static final String TASK_GROUP = 'Vagrant'

    /**
     * The Vagrant command to run.
     */
    @Input
    List<String> commands

    /**
     * The directory the targeted Vagrant box resides in.
     */
    @PathSensitive(PathSensitivity.RELATIVE)
    @InputDirectory
    File boxDir

    /**
     * The environment variables passed to Vagrant command.
     */
    @Input
    Map<String, String> environmentVariables = [:]

    @Internal
    ExternalProcessExecutor processExecutor

    Vagrant() {
        group = TASK_GROUP
        processExecutor = new GDKExternalProcessExecutor()
    }

    @TaskAction
    void runCommand() {
        List<String> vagrantCommands = getCommands()
        vagrantCommands.addAll(0, ExternalProgram.VAGRANT.commandLineArgs)
        vagrantCommands.addAll(getOptions())

        ExternalProcessExecutionResult result = processExecutor.execute(vagrantCommands, getEnvVars(), getBoxDir())

        if(!result.isOK()) {
            throw new GradleException('Failed to execute the Vagrant command.')
        }
    }

    @Internal
    List<String> getEnvVars() {
        getEnvironmentVariables().size() > 0 ? OsUtils.prepareEnvVars(getEnvironmentVariables()) : null
    }

    List<String> getOptions() {
        []
    }
}

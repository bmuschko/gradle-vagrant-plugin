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
import groovy.transform.PackageScope
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

abstract class Vagrant extends DefaultTask {
    static final String TASK_GROUP = 'Vagrant'

    /**
     * The Vagrant command to run.
     */
    @Input
    abstract ListProperty<String> getCommands()

    @Input
    abstract ListProperty<String> getOptions()

    /**
     * The directory the targeted Vagrant box resides in.
     */
    @PathSensitive(PathSensitivity.RELATIVE)
    @InputDirectory
    abstract DirectoryProperty getBoxDir()

    /**
     * The environment variables passed to Vagrant command.
     */
    @Input
    abstract MapProperty<String, String> getEnvironmentVariables()

    // visible for testing
    @PackageScope
    ExternalProcessExecutor processExecutor

    Vagrant() {
        group = TASK_GROUP
        processExecutor = new GDKExternalProcessExecutor()
    }

    @TaskAction
    void runCommand() {
        List<String> vagrantCommands = []
        vagrantCommands.addAll(commands.get())
        vagrantCommands.addAll(0, ExternalProgram.VAGRANT.commandLineArgs)
        vagrantCommands.addAll(options.get())

        ExternalProcessExecutionResult result = processExecutor.execute(vagrantCommands, getEnvVars(), boxDir.get().asFile)

        if (!result.isOK()) {
            throw new GradleException('Failed to execute the Vagrant command.')
        }
    }

    private List<String> getEnvVars() {
        def userSuppliedEnv = environmentVariables.get()
        return userSuppliedEnv.size() > 0 ? OsUtils.prepareEnvVars(userSuppliedEnv) : null
    }

}

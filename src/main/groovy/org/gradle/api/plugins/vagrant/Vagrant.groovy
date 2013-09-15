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

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class Vagrant extends DefaultTask {
    static final String TASK_GROUP = 'Vagrant'
    static final String VAGRANT_EXECUTABLE = 'vagrant'

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

    Vagrant() {
        group = TASK_GROUP
    }

    @TaskAction
    void start() {
        checkIfVagrantIsInstalled()
        runCommand()
    }

    private void checkIfVagrantIsInstalled() {
        def process = "$VAGRANT_EXECUTABLE -v".execute()
        process.waitFor()

        if(process.exitValue() != 0) {
            throw new GradleException("Vagrant could not be detected. Please install!")
        }

        logger.info "Using ${process.text.trim()}."
    }

    void runCommand() {
        List<String> vagrantCommands = getCommands()
        vagrantCommands.add(0, VAGRANT_EXECUTABLE)
        vagrantCommands.addAll(getOptions())
        logger.info "Executing Vagrant command: '${vagrantCommands.join(' ')}'"

        def process = vagrantCommands.execute(null, getBoxDir())
        process.consumeProcessOutput(System.out, System.err)
        process.waitFor()

        if(process.exitValue() != 0) {
            throw new GradleException("Failed to run the Vagrant command.")
        }
    }

    List<String> getOptions() {
        []
    }
}

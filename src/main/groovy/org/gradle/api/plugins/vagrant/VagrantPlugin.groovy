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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.plugins.vagrant.internal.PrerequisitesValidator
import org.gradle.api.plugins.vagrant.internal.VagrantInstallationValidator
import org.gradle.api.plugins.vagrant.tasks.Vagrant

class VagrantPlugin implements Plugin<Project> {
    static final String EXTENSION_NAME = 'vagrant'

    PrerequisitesValidator externalProgramValidator

    VagrantPlugin() {
        this.externalProgramValidator = new VagrantInstallationValidator()
    }

    @Override
    void apply(Project project) {
        project.extensions.create(EXTENSION_NAME, VagrantExtension)
        validateVagrantInstallation(project)
        addTasks(project)
    }

    private void validateVagrantInstallation(Project project) {
        project.gradle.taskGraph.whenReady { TaskExecutionGraph taskGraph ->
            if(containsVagrantTask(taskGraph)) {
                externalProgramValidator.validate()
            }
        }
    }

    private boolean containsVagrantTask(TaskExecutionGraph taskGraph) {
        taskGraph.allTasks.findAll { task -> task instanceof Vagrant }.size() > 0
    }

    private void addTasks(Project project) {
        project.tasks.withType(Vagrant).whenTaskAdded {
            conventionMapping.boxDir = { getBoxDir(project) }
        }

        VagrantTaskDefinition.values().each { taskDef ->
            project.task(taskDef.name, type: Vagrant, description: taskDef.description) {
                commands = taskDef.commands
            }
        }
    }

    private File getBoxDir(Project project) {
        project.hasProperty('boxDir') ? project.file(project.boxDir) : project.extensions.findByName(EXTENSION_NAME).boxDir
    }

    private enum VagrantTaskDefinition {
        DESTROY('destroy', 'Stops the running machine Vagrant is managing and destroys all resources.', ['destroy', '--force']),
        HALT('halt', 'Shuts down the running machine Vagrant is managing.', ['halt']),
        RELOAD('reload', 'The equivalent of running a halt followed by an up.', ['reload']),
        RESUME('resume', 'Resumes a Vagrant managed machine that was previously suspended.', ['resume']),
        SSH_CONFIG('sshConfig', 'Outputs the valid configuration for an SSH config file to SSH.', ['ssh-config']),
        STATUS('status', 'Outputs the state of the machines Vagrant is managing.', ['status']),
        SUSPEND('suspend', 'Suspends the guest machine Vagrant is managing.', ['suspend']),
        UP('up', 'Creates and configures guest machines according to your Vagrantfile.', ['up'])

        private final String name
        private final String description
        private final List<String> commands

        private VagrantTaskDefinition(String name, String description, List<String> commands) {
            this.name = name
            this.description = description
            this.commands = commands
        }

        String getName() {
            "vagrant${name.capitalize()}"
        }
    }
}
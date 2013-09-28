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

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.plugins.vagrant.tasks.Vagrant
import org.gradle.api.plugins.vagrant.tasks.VagrantUp
import org.gradle.api.plugins.vagrant.validation.AggregatingPrerequisitesValidator
import org.gradle.api.plugins.vagrant.validation.PrerequisitesValidationResult

class VagrantPlugin implements Plugin<Project> {
    static final String EXTENSION_NAME = 'vagrant'

    AggregatingPrerequisitesValidator prerequisitesValidator

    VagrantPlugin() {
        prerequisitesValidator = new AggregatingPrerequisitesValidator()
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
                String requestedProvider = getProvider(project)

                if(requestedProvider) {
                    prerequisitesValidator.setProvider(requestedProvider)
                }

                PrerequisitesValidationResult result = prerequisitesValidator.validate()

                if(!result.success) {
                    throw new GradleException(result.message)
                }
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

        project.tasks.withType(VagrantUp).whenTaskAdded {
            conventionMapping.provider = { getProvider(project) }
        }

        VagrantTaskDefinition.values().each { taskDef ->
            project.task(taskDef.name, type: taskDef.taskClass, description: taskDef.description) {
                commands = taskDef.commands
            }
        }
    }

    private File getBoxDir(Project project) {
        project.hasProperty('boxDir') ? project.file(project.boxDir) : project.extensions.findByName(EXTENSION_NAME).boxDir
    }

    private String getProvider(Project project) {
        project.hasProperty('provider') ? project.provider : project.extensions.findByName(EXTENSION_NAME).provider
    }

    private enum VagrantTaskDefinition {
        DESTROY(Vagrant, 'destroy', 'Stops the running machine Vagrant is managing and destroys all resources.', ['destroy', '--force']),
        HALT(Vagrant, 'halt', 'Shuts down the running machine Vagrant is managing.', ['halt']),
        RELOAD(Vagrant, 'reload', 'The equivalent of running a halt followed by an up.', ['reload']),
        RESUME(Vagrant, 'resume', 'Resumes a Vagrant managed machine that was previously suspended.', ['resume']),
        SSH_CONFIG(Vagrant, 'sshConfig', 'Outputs the valid configuration for an SSH config file to SSH.', ['ssh-config']),
        STATUS(Vagrant, 'status', 'Outputs the state of the machines Vagrant is managing.', ['status']),
        SUSPEND(Vagrant, 'suspend', 'Suspends the guest machine Vagrant is managing.', ['suspend']),
        UP(VagrantUp, 'up', 'Creates and configures guest machines according to your Vagrantfile.', ['up'])

        private final Class taskClass
        private final String name
        private final String description
        private final List<String> commands

        private VagrantTaskDefinition(Class taskClass, String name, String description, List<String> commands) {
            this.taskClass = taskClass
            this.name = name
            this.description = description
            this.commands = commands
        }

        String getName() {
            "vagrant${name.capitalize()}"
        }
    }
}
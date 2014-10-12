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
package com.bmuschko.gradle.vagrant

import com.bmuschko.gradle.vagrant.tasks.*
import org.gradle.api.Plugin
import org.gradle.api.Project

class VagrantPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.plugins.apply(VagrantBasePlugin)
        addTasks(project)
    }

    private void addTasks(Project project) {
        VagrantTaskDefinition.values().each { taskDef ->
            project.task(taskDef.name, type: taskDef.taskClass, description: taskDef.description)
        }
    }

    private enum VagrantTaskDefinition {
        DESTROY(VagrantDestroy, 'destroy', 'Stops the running machine Vagrant is managing and destroys all resources.'),
        HALT(VagrantHalt, 'halt', 'Shuts down the running machine Vagrant is managing.'),
        RELOAD(VagrantReload, 'reload', 'The equivalent of running a halt followed by an up.'),
        RESUME(VagrantResume, 'resume', 'Resumes a Vagrant managed machine that was previously suspended.'),
        SSH_CONFIG(VagrantSshConfig, 'sshConfig', 'Outputs the valid configuration for an SSH config file to SSH.'),
        STATUS(VagrantStatus, 'status', 'Outputs the state of the machines Vagrant is managing.'),
        SUSPEND(VagrantSuspend, 'suspend', 'Suspends the guest machine Vagrant is managing.'),
        UP(VagrantUp, 'up', 'Creates and configures guest machines according to your Vagrantfile.')

        private final Class taskClass
        private final String name
        private final String description

        private VagrantTaskDefinition(Class taskClass, String name, String description) {
            this.taskClass = taskClass
            this.name = name
            this.description = description
        }

        String getName() {
            "vagrant${name.capitalize()}"
        }
    }
}
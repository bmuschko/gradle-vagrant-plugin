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
import com.bmuschko.gradle.vagrant.tasks.VagrantUp
import com.bmuschko.gradle.vagrant.validation.AggregatingPrerequisitesValidator
import com.bmuschko.gradle.vagrant.validation.PrerequisitesValidationResult
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.execution.TaskExecutionGraph

class VagrantBasePlugin implements Plugin<Project> {
    static final String EXTENSION_NAME = 'vagrant'

    AggregatingPrerequisitesValidator prerequisitesValidator

    VagrantBasePlugin() {
        prerequisitesValidator = new AggregatingPrerequisitesValidator()
    }

    @Override
    void apply(Project project) {
        project.extensions.create(EXTENSION_NAME, VagrantExtension)
        configureVagrantTasks(project)      
        validateVagrantInstallation(project)
    }

    private void configureVagrantTasks(Project project) {
        project.tasks.withType(Vagrant).configureEach {
            it.boxDir.convention(project.objects.directoryProperty().fileValue(getBoxDir(project)))
            it.environmentVariables.convention(project.extensions.findByName(EXTENSION_NAME).environmentVariables.variables)
        }

        project.tasks.withType(VagrantUp).configureEach {
            it.provider.convention(getProvider(project))
        }
    }

    private File getBoxDir(Project project) {
        File boxDir = project.hasProperty('boxDir') ? project.file(project.boxDir) : project.extensions.findByName(EXTENSION_NAME).boxDir
        boxDir ?: project.file("vagrant")
    }

    private String getProvider(Project project) {
        String provider = project.hasProperty('provider') ? project.provider : project.extensions.findByName(EXTENSION_NAME).provider
        provider ?: Provider.VIRTUALBOX.name
    }

    private void validateVagrantInstallation(Project project) {
        project.gradle.taskGraph.whenReady { TaskExecutionGraph taskGraph ->
            if(isInstallationValidationEnabled(project) && containsVagrantTask(taskGraph)) {
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

    private Boolean isInstallationValidationEnabled(Project project) {
        Boolean enabledValidation = project.extensions.findByName(EXTENSION_NAME).installation.validate
        project.logger.info "Installation validation enabled: $enabledValidation"
        enabledValidation
    }

    private boolean containsVagrantTask(TaskExecutionGraph taskGraph) {
        taskGraph.allTasks.findAll { task -> task instanceof Vagrant }.size() > 0
    }
}

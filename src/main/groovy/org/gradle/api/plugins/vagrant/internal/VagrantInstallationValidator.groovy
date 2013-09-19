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
package org.gradle.api.plugins.vagrant.internal

import groovy.util.logging.Slf4j
import org.gradle.api.GradleException
import org.gradle.api.plugins.vagrant.tasks.Vagrant

@Slf4j
class VagrantInstallationValidator implements PrerequisitesValidator {
    ExternalProcessExecutor externalProcessExecutor

    VagrantInstallationValidator() {
        this.externalProcessExecutor = new GDKExternalProcessExecutor()
    }

    @Override
    void validate() {
        try {
            ExternalProcessExecutionResult result = externalProcessExecutor.execute([Vagrant.EXECUTABLE, '-v'])

            if(!result.isOK()) {
                throw new GradleException('Vagrant is not functional. Please check!')
            }

            log.debug "Using ${result.text.trim()}."
        }
        catch (IOException e) {
            throw new GradleException('Vagrant could not be detected. Please install!')
        }
    }
}

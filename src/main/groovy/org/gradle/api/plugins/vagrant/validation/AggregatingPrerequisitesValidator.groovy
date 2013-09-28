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
package org.gradle.api.plugins.vagrant.validation

class AggregatingPrerequisitesValidator implements PrerequisitesValidator, BackendProviderAware {
    PrerequisitesValidator vagrantInstallationValidator
    String provider
    def installationValidators

    AggregatingPrerequisitesValidator() {
        vagrantInstallationValidator = new VagrantInstallationValidator()
        installationValidators = [vagrantInstallationValidator]
    }

    @Override
    PrerequisitesValidationResult validate() {
        if(provider) {
            installationValidators << new VirtualBoxInstallationValidator()
        }

        for(PrerequisitesValidator validator : installationValidators) {
            PrerequisitesValidationResult result = validator.validate()

            if(!result.success) {
                return result
            }
        }

        new PrerequisitesValidationResult(success: true, message: 'Prerequisites are correctly installed.')
    }

    @Override
    void setProvider(String provider) {
        this.provider = provider
    }
}

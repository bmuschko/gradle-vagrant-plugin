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
package com.bmuschko.gradle.vagrant.validation

import com.bmuschko.gradle.vagrant.process.ExternalProcessExecutionResult
import com.bmuschko.gradle.vagrant.process.ExternalProcessExecutor
import com.bmuschko.gradle.vagrant.process.ExternalProgram
import org.gradle.api.GradleException
import spock.lang.Specification

class VagrantInstallationValidatorSpec extends Specification {
    PrerequisitesValidator vagrantInstallationValidator
    ExternalProcessExecutor mockExternalProcessExecutor

    def setup() {
        vagrantInstallationValidator = new VagrantInstallationValidator()
        mockExternalProcessExecutor = Mock()
        vagrantInstallationValidator.externalProcessExecutor = mockExternalProcessExecutor
    }

    def "Validate for thrown exception"() {
        when:
            vagrantInstallationValidator.validate()
        then:
            1 * mockExternalProcessExecutor.execute([ExternalProgram.VAGRANT.executable, '-v']) >> { throw new IOException("something is wrong") }
            Throwable t = thrown(GradleException)
            t.message == 'Vagrant could not be detected. Please install!'
    }

    def "Validate for incorrect Vagrant installation"() {
        expect:
            ExternalProcessExecutionResult executionResult = new ExternalProcessExecutionResult(exitValue: 1, text: 'failure')
        when:
            PrerequisitesValidationResult validationResult = vagrantInstallationValidator.validate()
        then:
            1 * mockExternalProcessExecutor.execute([ExternalProgram.VAGRANT.executable, '-v']) >> executionResult
            !validationResult.success
            validationResult.message == 'Vagrant is not functional. Please check!'
    }

    def "Validate for correct Vagrant installation"() {
        expect:
            ExternalProcessExecutionResult executionResult = new ExternalProcessExecutionResult(exitValue: 0, text: 'Vagrant version 1.5.5')
        when:
            PrerequisitesValidationResult validationResult = vagrantInstallationValidator.validate()
        then:
            1 * mockExternalProcessExecutor.execute([ExternalProgram.VAGRANT.executable, '-v']) >> executionResult
            validationResult.success
            validationResult.message == 'Vagrant version 1.5.5'
    }
}

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

import org.gradle.api.GradleException
import org.gradle.api.plugins.vagrant.process.ExternalProcessExecutionResult
import org.gradle.api.plugins.vagrant.process.ExternalProcessExecutor
import org.gradle.api.plugins.vagrant.process.ExternalProgram
import spock.lang.Specification

class VirtualBoxInstallationValidatorSpec extends Specification {
    PrerequisitesValidator virtualBoxInstallationValidator
    ExternalProcessExecutor mockExternalProcessExecutor

    def setup() {
        virtualBoxInstallationValidator = new VirtualBoxInstallationValidator()
        mockExternalProcessExecutor = Mock()
        virtualBoxInstallationValidator.externalProcessExecutor = mockExternalProcessExecutor
    }

    def "Validate for thrown exception"() {
        when:
            virtualBoxInstallationValidator.validate()
        then:
            1 * mockExternalProcessExecutor.execute([ExternalProgram.VIRTUALBOX.executable, '-v']) >> { throw new IOException("something is wrong") }
            Throwable t = thrown(GradleException)
            t.message == 'VirtualBox could not be detected. Please install!'
    }

    def "Validate for incorrect VirtualBox installation"() {
        expect:
            ExternalProcessExecutionResult executionResult = new ExternalProcessExecutionResult(exitValue: 1, text: 'failure')
        when:
            PrerequisitesValidationResult validationResult = virtualBoxInstallationValidator.validate()
        then:
            1 * mockExternalProcessExecutor.execute([ExternalProgram.VIRTUALBOX.executable, '-v']) >> executionResult
            !validationResult.success
            validationResult.message == 'VirtualBox is not functional. Please check!'
    }

    def "Validate for correct VirtualBox installation"() {
        expect:
            ExternalProcessExecutionResult executionResult = new ExternalProcessExecutionResult(exitValue: 0, text: 'VirtualBox version 1.5.5')
        when:
            PrerequisitesValidationResult validationResult = virtualBoxInstallationValidator.validate()
        then:
            1 * mockExternalProcessExecutor.execute([ExternalProgram.VIRTUALBOX.executable, '-v']) >> executionResult
            validationResult.success
            validationResult.message == 'VirtualBox version 1.5.5'
    }
}

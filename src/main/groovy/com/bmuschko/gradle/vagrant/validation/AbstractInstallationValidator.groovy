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
import com.bmuschko.gradle.vagrant.process.GDKExternalProcessExecutor
import org.gradle.api.GradleException

abstract class AbstractInstallationValidator implements PrerequisitesValidator {
    ExternalProcessExecutor externalProcessExecutor

    AbstractInstallationValidator() {
        externalProcessExecutor = new GDKExternalProcessExecutor(false)
    }

    @Override
    PrerequisitesValidationResult validate() {
        ExternalProgram externalProgram = getExternalProgram()
        List<String> commands = externalProgram.commandLineArgs
        commands.addAll(getExecutableOptions())

        try {
            ExternalProcessExecutionResult result = externalProcessExecutor.execute(commands)
            String message = result.isOK() ? result.text.trim() : "$externalProgram.name is not functional. Please check!"
            handleResult(result)
            return new PrerequisitesValidationResult(success: result.isOK(), message: message)
        }
        catch(IOException e) {
            throw new GradleException("$externalProgram.name could not be detected. Please install!")
        }
    }

    abstract ExternalProgram getExternalProgram()
    abstract List<String> getExecutableOptions()
    abstract void handleResult(ExternalProcessExecutionResult result)
}

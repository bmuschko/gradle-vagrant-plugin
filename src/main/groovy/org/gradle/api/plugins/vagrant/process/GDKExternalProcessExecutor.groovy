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
package org.gradle.api.plugins.vagrant.process

import groovy.util.logging.Slf4j

@Slf4j
class GDKExternalProcessExecutor implements ExternalProcessExecutor {
    private final OutputStream output
    private final OutputStream error

    GDKExternalProcessExecutor() {}

    GDKExternalProcessExecutor(OutputStream output, OutputStream error) {
        this.output = output
        this.error = error
    }

    @Override
    ExternalProcessExecutionResult execute(List<String> commands) throws IOException {
        printCommandLineArgs(commands)
        Process process = commands.execute()
        handleProcess(process)
    }

    @Override
    ExternalProcessExecutionResult execute(List<String> commands, List envp, File dir) throws IOException {
        printCommandLineArgs(commands)
        Process process = commands.execute(envp, dir)
        handleProcess(process)
    }

    private void printCommandLineArgs(List<String> commands) {
        log.info "Executing external command: '${commands.join(' ')}'"
    }

    private ExternalProcessExecutionResult handleProcess(Process process) {
        if(output && error) {
            process.consumeProcessOutput(output, error)
        }

        process.waitFor()
        new ExternalProcessExecutionResult(exitValue: process.exitValue(), text: process.text)
    }
}

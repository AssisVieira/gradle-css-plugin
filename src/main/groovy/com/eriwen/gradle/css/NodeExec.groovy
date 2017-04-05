package com.eriwen.gradle.css

import org.gradle.api.Project
import org.gradle.process.ExecResult

/**
 * Utility for executing JS with Node.
 *
 * @author Assis Vieira
 * @date 4/05/17
 */
class NodeExec {

    private static final String NODE_EXEC = 'node'

    private Project project

    void execute(final Iterable<String> execargs, final Map<String, Object> options = [:]) {
        final String workingDirIn = options.get('workingDir', '.')
        final Boolean ignoreExitCode = options.get('ignoreExitCode', false).asBoolean()
        final OutputStream out = options.get('out', System.out) as OutputStream

        println("exec "+ NODE_EXEC + " " + execargs.join(" "))

        def execOptions = {
            commandLine = NODE_EXEC
            args = execargs
            workingDir = workingDirIn
            ignoreExitValue = ignoreExitCode
            standardOutput = out
        }

        ExecResult result = project.exec(execOptions)

        if (!ignoreExitCode) {
            result.assertNormalExitValue()
        }
    }

    public NodeExec(final Project projectIn) {
        project = projectIn
    }
}

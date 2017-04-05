/**
 * Copyright 2013 Joe Fitzgerald
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.eriwen.gradle.css.tasks

import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import com.eriwen.gradle.css.ResourceUtil
import com.eriwen.gradle.css.NodeExec

class LessTask extends SourceTask {

    @OutputDirectory
    def dest

    File getDest() {
        project.file(dest)
    }

    @TaskAction
    def run() {
        logger.debug "Processing ${source.files.size()} files"

        source.visit { visitDetail ->
            if (visitDetail.directory) {
                visitDetail.relativePath.getFile(getDest()).mkdir()
            } else {
                if (visitDetail.name.endsWith(".less")) {
                    if (!visitDetail.name.startsWith("_")) {
                        def relativePathToCss = visitDetail.relativePath.replaceLastName(visitDetail.name.replace(".less", ".css"))
                        File outputFile = relativePathToCss.getFile(getDest())
                        compileLess(visitDetail.file, outputFile)
                    }
                } else {
                    logger.debug("Copying non-less resource ${visitDetail.file.absolutePath} to ${getDest().absolutePath}")
                    visitDetail.copyTo(visitDetail.relativePath.getFile(getDest()))
                }
            }
        }
    }

    private static final ResourceUtil RESOURCE_UTIL = new ResourceUtil()
    private static final String LESSC_DIR = "lessc"
    private static final String TMP_DIR = "tmp${File.separator}lessc"
    private final NodeExec node = new NodeExec(project)

    def compileLess(src, target) {
        logger.debug "Processing ${src.canonicalPath} to ${target.canonicalPath}"
        try {
            final File lesscDir = RESOURCE_UTIL.extractDirToDirectory(
                    new File(project.buildDir, TMP_DIR), LESSC_DIR)
            final List<String> args = [lesscDir.canonicalPath + "/bin/lessc"]
            args.add(src.canonicalPath)
            node.execute(args, [out: new FileOutputStream(target)])

        } catch (e) {
            throw new RuntimeException(e)
        }
    }
}

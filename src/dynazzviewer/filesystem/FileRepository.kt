package dynazzviewer.filesystem

import dynazzviewer.services.filesystem.VideoFile

interface FileRepository {
    fun add(rootPath: String): List<VideoFile>

    fun remove(rootPath: String): Boolean
}

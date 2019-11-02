package dynazzviewer.filesystem

import dynazzviewer.filesystem.hierarchy.FileName
import dynazzviewer.filesystem.hierarchy.RootDirectory
import dynazzviewer.services.filesystem.VideoFile

interface FileRepository {
    fun add(rootPath: String): List<VideoFile>

    fun remove(rootPath: String): Boolean

    fun updateFileName(fileName: FileName): Map<RootDirectory, Set<VideoFile>>
}

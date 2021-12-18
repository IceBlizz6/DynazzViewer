package dynazzviewer.filesystem

import dynazzviewer.filesystem.hierarchy.FileName
import dynazzviewer.filesystem.hierarchy.RootDirectory
import dynazzviewer.services.filesystem.VideoFile

interface FileRepository {
    fun list(): Map<String, Set<VideoFile>>

    fun add(rootPath: String): Set<VideoFile>

    fun remove(rootPath: String): Boolean

    fun refreshDirectory(directoryPath: String): Map<RootDirectory, Set<VideoFile>>

    fun updateFileName(fileName: FileName): Map<RootDirectory, Set<VideoFile>>
}

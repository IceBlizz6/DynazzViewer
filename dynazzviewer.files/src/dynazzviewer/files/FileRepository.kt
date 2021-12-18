package dynazzviewer.files

import dynazzviewer.files.hierarchy.FileName
import dynazzviewer.files.hierarchy.RootDirectory
import dynazzviewer.files.hierarchy.VideoFile

interface FileRepository {
    fun list(): Map<String, Set<VideoFile>>

    fun add(rootPath: String): Set<VideoFile>

    fun remove(rootPath: String): Boolean

    fun refreshDirectory(directoryPath: String): Map<RootDirectory, Set<VideoFile>>

    fun updateFileName(fileName: FileName): Map<RootDirectory, Set<VideoFile>>
}

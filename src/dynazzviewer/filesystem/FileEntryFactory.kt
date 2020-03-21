package dynazzviewer.filesystem

import dynazzviewer.base.ViewStatus
import dynazzviewer.filesystem.hierarchy.FilePath
import dynazzviewer.filesystem.hierarchy.RootDirectory
import dynazzviewer.services.filesystem.VideoFile
import dynazzviewer.storage.Storage

class FileEntryFactory(
    private val configuration: FileConfiguration,
    private val storage: Storage
) {
    fun files(root: RootDirectory, filePaths: Set<FilePath>): Set<VideoFile> {
        storage.read().use { context ->
            val names = filePaths.map { e -> e.fileName.name }.toSet()
            val storageLookup: Map<String, Pair<Int, ViewStatus>> = context.mediaFilesByName(names)
            val videoFiles = mutableSetOf<VideoFile>()
            for (filePath in filePaths) {
                val extension = filePath.fileName.extension
                if (configuration.videoExtensions.contains(extension)) {
                    val fileName = filePath.fileName
                    val entryLookup: Pair<Int, ViewStatus>? = storageLookup[fileName.name]
                    val videoFile = VideoFile(
                        root = root,
                        name = fileName,
                        path = filePath,
                        viewStatus = entryLookup?.second ?: ViewStatus.None,
                        mediaFileId = entryLookup?.first
                    )
                    videoFiles.add(videoFile)
                }
            }
            return videoFiles
        }
    }
}

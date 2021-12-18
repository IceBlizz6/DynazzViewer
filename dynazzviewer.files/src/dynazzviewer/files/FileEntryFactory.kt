package dynazzviewer.files

import dynazzviewer.base.ViewStatus
import dynazzviewer.entities.MediaFile
import dynazzviewer.filesystem.FileConfiguration
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
            val storageLookup: Map<String, MediaFile> = context.mediaFilesByName(names)
            val videoFiles = mutableSetOf<VideoFile>()
            for (filePath in filePaths) {
                val extension = filePath.fileName.extension
                if (configuration.videoExtensions.contains(extension)) {
                    val fileName = filePath.fileName
                    val entryLookup: MediaFile? = storageLookup[fileName.name]
                    val videoFile = VideoFile(
                        root = root,
                        fileName = fileName,
                        filePath = filePath,
                        viewStatus = entryLookup?.status ?: ViewStatus.None,
                        mediaFileId = entryLookup?.id,
                        linkedMediaPartId = entryLookup?.mediaPart?.id
                    )
                    videoFiles.add(videoFile)
                }
            }
            return videoFiles
        }
    }
}

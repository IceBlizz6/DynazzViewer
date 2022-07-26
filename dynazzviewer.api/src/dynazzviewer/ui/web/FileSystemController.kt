package dynazzviewer.ui.web

import dynazzviewer.controllers.UpdateListener
import dynazzviewer.controllers.ViewStatusController
import dynazzviewer.entities.ViewStatus
import dynazzviewer.files.FileConfiguration
import dynazzviewer.files.FileRepository
import dynazzviewer.files.hierarchy.FilePath
import dynazzviewer.files.hierarchy.RootDirectory
import dynazzviewer.files.hierarchy.VideoFile
import dynazzviewer.storage.Storage
import java.awt.Desktop
import java.io.File

class FileSystemController(
    private val storage: Storage,
    private val fileConfiguration: FileConfiguration,
    private val fileRepository: FileRepository
) : UpdateListener {
    private val desktop: Desktop = Desktop.getDesktop()
    private val viewStatusController: ViewStatusController

    init {
        for (path in fileConfiguration.rootDirectoryPaths) {
            fileRepository.add(path)
        }
        this.viewStatusController = ViewStatusController(storage, this)
    }

    fun list(): Map<String, Set<VideoFile>> {
        return fileRepository.list()
    }

    fun addRootDirectory(path: String) {
        val storedPaths = fileConfiguration.rootDirectoryPaths.toMutableSet()
        val sanitizedPath = path.replace(File.separator, "/")
        storedPaths.add(sanitizedPath)
        fileConfiguration.rootDirectoryPaths = storedPaths
        fileRepository.add(sanitizedPath)
    }

    fun removeRootDirectory(path: String) {
        val storedPaths = fileConfiguration.rootDirectoryPaths.toMutableSet()
        storedPaths.remove(path)
        fileConfiguration.rootDirectoryPaths = storedPaths
        fileRepository.remove(path)
    }

    fun refreshDirectory(directoryPath: String): Map<RootDirectory, Set<VideoFile>> {
        return fileRepository.refreshDirectory(directoryPath)
    }

    fun setViewStatus(videoFilePaths: Set<String>, status: ViewStatus): Map<String, Int> {
        return videoFilePaths.associate { path ->
            val name = FilePath(path).fileName.name
            name to viewStatusController.setMediaFileStatus(status, name)
        }
    }

    fun playVideo(videoFilePath: String) {
        Desktop.getDesktop().open(File(videoFilePath))
    }

    fun showExplorer(path: String) {
        val target = File(path)
        if (target.isFile) {
            desktop.open(target.parentFile)
        } else {
            desktop.open(target)
        }
    }

    fun linkVideoFile(mediaFileId: Int, mediaPartId: Int) {
        viewStatusController.link(
            mediaFileId = mediaFileId,
            mediaPartId = mediaPartId
        )
    }

    fun linkVideoFile(mediaFileName: String, mediaPartId: Int) {
        val mediaFileId = viewStatusController.getOrCreateMediaFile(mediaFileName)
        viewStatusController.link(
            mediaFileId = mediaFileId,
            mediaPartId = mediaPartId
        )
    }

    override fun updateMediaUnit(id: Int, recursive: Boolean) = Unit

    override fun updateMediaPart(id: Int) = Unit

    override fun updateMediaFile(id: Int) = Unit

    override fun setMediaFileId(name: String, assignedId: Int) = Unit
}

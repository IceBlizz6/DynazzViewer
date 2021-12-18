package dynazzviewer.ui.web

import dynazzviewer.controllers.UpdateListener
import dynazzviewer.controllers.ViewStatusController
import dynazzviewer.entities.ViewStatus
import dynazzviewer.filesystem.FileConfiguration
import dynazzviewer.filesystem.FileRepository
import dynazzviewer.filesystem.hierarchy.FilePath
import dynazzviewer.filesystem.hierarchy.RootDirectory
import dynazzviewer.services.filesystem.VideoFile
import dynazzviewer.storage.Storage
import dynazzviewer.ui.config.UserConfiguration
import java.awt.Desktop
import java.io.File

class FileSystemController(
    private val storage: Storage,
    private val fileConfiguration: FileConfiguration,
    private val userConfiguration: UserConfiguration,
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
        return videoFilePaths
            .map { path ->
                run {
                    val name = FilePath(path).fileName.name
                    name to viewStatusController.setMediaFileStatus(status, name)
                }
            }
            .toMap()
    }

    fun playVideos(videoFilePaths: List<String>): Boolean {
        val applicationPath: String? = userConfiguration.mediaPlayerApplicationPath
        if (applicationPath != null) {
            val videoPaths = videoFilePaths.joinToString(" ") { "\"" + it + "\"" }
            Runtime.getRuntime().exec("$applicationPath $videoPaths")
            return true
        } else {
            return false
        }
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

    val playVideoEnabled: Boolean
        get() = userConfiguration.mediaPlayerApplicationPath != null

    override fun updateMediaUnit(id: Int, recursive: Boolean) = Unit

    override fun updateMediaPart(id: Int) = Unit

    override fun updateMediaFile(id: Int) = Unit

    override fun setMediaFileId(name: String, assignedId: Int) = Unit
}

package dynazzviewer.ui.tornado

import dynazzviewer.base.ViewStatus
import dynazzviewer.controllers.UpdateListener
import dynazzviewer.controllers.ViewStatusController
import dynazzviewer.filesystem.FileConfiguration
import dynazzviewer.filesystem.FileRepository
import dynazzviewer.filesystem.hierarchy.FileName
import dynazzviewer.filesystem.hierarchy.FilePath
import dynazzviewer.storage.Storage
import dynazzviewer.ui.config.UserConfiguration
import dynazzviewer.ui.tornado.viewmodels.DirectoryViewModel
import dynazzviewer.ui.tornado.viewmodels.NodeFactory
import dynazzviewer.ui.tornado.viewmodels.NodeViewModel
import dynazzviewer.ui.tornado.viewmodels.RootNodeViewModel
import dynazzviewer.ui.tornado.viewmodels.VideoFileViewModel
import java.awt.Desktop
import java.io.File
import java.nio.file.Paths

class FileSystemController(
    private val storage: Storage,
    private val fileConfiguration: FileConfiguration,
    private val userConfiguration: UserConfiguration,
    private val fileRepository: FileRepository
) : UpdateListener {
    val fileSystemRoot: RootNodeViewModel = RootNodeViewModel()
    private val desktop: Desktop = Desktop.getDesktop()

    init {
        for (path in fileConfiguration.rootDirectoryPaths) {
            loadRootDirectory(path)
        }
    }

    fun addRootDirectories(directories: List<File>) {
        for (directory in directories) {
            val path = directory.canonicalPath
            if (!fileConfiguration.rootDirectoryPaths.contains(path)) {
                addRootDirectory(path)
            }
        }
    }

    private fun addRootDirectory(path: String) {
        val storedPaths = fileConfiguration.rootDirectoryPaths.toMutableSet()
        storedPaths.add(path)
        fileConfiguration.rootDirectoryPaths = storedPaths
        loadRootDirectory(path)
    }

    fun removeRootDirectory(path: String) {
        val storedPaths = fileConfiguration.rootDirectoryPaths.toMutableSet()
        storedPaths.remove(path)
        fileConfiguration.rootDirectoryPaths = storedPaths
        fileRepository.remove(path)
        val childNode = fileSystemRoot.children.single { it.name == path }
        fileSystemRoot.children.remove(childNode)
    }

    private fun loadRootDirectory(path: String) {
        val factory = NodeFactory(fileRepository)
        val rootDir = factory.createRootViewModel(path)
        fileSystemRoot.children.add(rootDir)
    }

    fun setVideoViewStatusRecursive(nodes: List<NodeViewModel>, status: ViewStatus) {
        val videoFiles = nodes
            .flatMap { it.videoFiles() }
            .distinct()
        setVideoViewStatus(videoFiles, status)
    }

    fun refreshDirectory(directory: DirectoryViewModel) {
        val factory = NodeFactory(fileRepository)
        factory.refreshDirectory(fileSystemRoot, FilePath(directory.fullPath))
    }

    fun setVideoViewStatus(videoFiles: List<VideoFileViewModel>, status: ViewStatus) {
        val distinctNames = videoFiles
            .map { it.name }
            .distinct()
        val controller = ViewStatusController(storage, this)
        for (fileName in distinctNames) {
            controller.setMediaFileStatus(status, fileName)
            val updatedVideoFiles = fileRepository.updateFileName(FileName(fileName))
            for (entry in updatedVideoFiles) {
                val root = entry.key
                val videoFileModels = entry.value
                for (fileModel in videoFileModels) {
                    val videoFileViewModel = findVideoFile(root.rootPath, fileModel.path.path)
                    videoFileViewModel.viewStatus = status
                }
            }
        }
    }

    private fun findVideoFile(rootPath: String, fullPath: String): VideoFileViewModel {
        val root = fileSystemRoot
            .children
            .single { it.name == rootPath }
        val substring = fullPath.removePrefix(rootPath)
        val viewModelPath = Paths.get(substring)
        var node = root
        for (path in viewModelPath) {
            node = node.children.single { it.name == path.fileName.toString() }
        }
        return node as VideoFileViewModel
    }

    fun playVideos(videoFiles: List<VideoFileViewModel>) {
        val applicationPath: String? = userConfiguration.mediaPlayerApplicationPath
        if (applicationPath != null) {
            val videoPaths = videoFiles.joinToString(" ") { "\"" + it.fullPath + "\"" }
            Runtime.getRuntime().exec("$applicationPath $videoPaths")
        }
    }

    fun showExplorer(nodes: List<NodeViewModel>) {
        val selectedDirectories = mutableSetOf<NodeViewModel>()
        selectedDirectories.addAll(
            nodes
                .filterIsInstance<VideoFileViewModel>()
                .map { it.parent }
        )
        selectedDirectories.addAll(
            nodes
                .filterIsInstance<DirectoryViewModel>()
        )
        selectedDirectories
            .map { it.fullPath }
            .map { File(it) }
            .forEach { desktop.open(it) }
    }

    val playVideoEnabled: Boolean
        get() = userConfiguration.mediaPlayerApplicationPath != null

    override fun updateMediaUnit(id: Int, recursive: Boolean) = Unit

    override fun updateMediaPart(id: Int) = Unit

    override fun updateMediaFile(id: Int) = Unit

    override fun setMediaFileId(name: String, assignedId: Int) = Unit
}

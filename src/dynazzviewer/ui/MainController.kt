package dynazzviewer.ui

import dynazzviewer.filesystem.FileCache
import dynazzviewer.filesystem.FileEntryFactory
import dynazzviewer.filesystem.FileRepository
import dynazzviewer.filesystem.FileSystemRepository
import dynazzviewer.filesystem.SystemFileSource
import dynazzviewer.storage.Storage
import dynazzviewer.storage.sqlite.SqlLiteStorage
import dynazzviewer.ui.viewmodels.NodeFactory
import dynazzviewer.ui.viewmodels.NodeViewModel
import dynazzviewer.ui.viewmodels.RootNodeViewModel
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import tornadofx.Controller

class MainController : Controller() {
    override val configPath: Path = Paths.get(
        DefaultConfiguration.userDirectory,
        DefaultConfiguration.configPropertiesFileName
    )

    private val configuration: DefaultConfiguration = DefaultConfiguration(config)

    val fileSystemRoot: NodeViewModel = RootNodeViewModel()

    private val storage: Storage

    private val fileRepository: FileRepository

    init {
        storage = SqlLiteStorage(configuration)
        val fileEntryFactory = FileEntryFactory(configuration, storage)
        val systemFileSource = SystemFileSource(configuration)
        val fileCache = FileCache(systemFileSource)
        fileRepository = FileSystemRepository(fileCache, fileEntryFactory)
        for (path in configuration.rootDirectoryPaths) {
            loadRootDirectory(path)
        }
    }

    fun addRootDirectories(directories: List<File>) {
        for (directory in directories) {
            val path = directory.canonicalPath
            if (!configuration.rootDirectoryPaths.contains(path)) {
                addRootDirectory(path)
            }
        }
    }

    private fun addRootDirectory(path: String) {
        val storedPaths = configuration.rootDirectoryPaths.toMutableSet()
        storedPaths.add(path)
        configuration.rootDirectoryPaths = storedPaths
        loadRootDirectory(path)
    }

    private fun loadRootDirectory(path: String) {
        val factory = NodeFactory(fileRepository)
        val rootDir = factory.createRootViewModel(path, true)
        fileSystemRoot.children.add(rootDir)
    }
}

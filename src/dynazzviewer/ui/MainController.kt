package dynazzviewer.ui

import dynazzviewer.filesystem.FileCache
import dynazzviewer.filesystem.FileEntryFactory
import dynazzviewer.filesystem.FileRepository
import dynazzviewer.filesystem.FileSystemRepository
import dynazzviewer.filesystem.SystemFileSource
import dynazzviewer.storage.Storage
import dynazzviewer.storage.sqlite.SqlLiteStorage
import java.nio.file.Path
import java.nio.file.Paths
import tornadofx.Controller

class MainController : Controller() {
    override val configPath: Path = Paths.get(
        DefaultConfiguration.userDirectory,
        DefaultConfiguration.configPropertiesFileName
    )

    private val configuration: DefaultConfiguration = DefaultConfiguration(config)

    private val storage: Storage

    private val fileRepository: FileRepository

    val fileSystemController: FileSystemController

    init {
        storage = SqlLiteStorage(configuration)
        val fileEntryFactory = FileEntryFactory(configuration, storage)
        val systemFileSource = SystemFileSource(configuration)
        val fileCache = FileCache(systemFileSource)
        fileRepository = FileSystemRepository(fileCache, fileEntryFactory)
        this.fileSystemController = FileSystemController(
            storage = storage,
            fileRepository = fileRepository,
            fileConfiguration = configuration,
            userConfiguration = configuration
        )
    }
}

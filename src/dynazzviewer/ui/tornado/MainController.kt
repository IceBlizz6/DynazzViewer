package dynazzviewer.ui.tornado

import dynazzviewer.controllers.ServiceDescriptorController
import dynazzviewer.filesystem.FileCache
import dynazzviewer.filesystem.FileEntryFactory
import dynazzviewer.filesystem.FileRepository
import dynazzviewer.filesystem.FileSystemRepository
import dynazzviewer.filesystem.SystemFileSource
import dynazzviewer.services.HttpWebJsonParser
import dynazzviewer.services.descriptors.jikan.JikanApi
import dynazzviewer.services.descriptors.tvmaze.TvMazeApi
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

    val configuration: DefaultConfiguration = DefaultConfiguration(config)

    private val storage: Storage

    private val fileRepository: FileRepository

    val fileSystemController: FileSystemController

    val serviceController: ServiceDescriptorController

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

        serviceController = ServiceDescriptorController(
            storage = storage,
            listener = fileSystemController,
            descriptorServices = listOf(
                JikanApi(
                    fetchRelated = true,
                    autoFillEpisodeAirDates = true,
                    autoFillEpisodes = true,
                    parser = HttpWebJsonParser()
                ),
                TvMazeApi(
                    parser = HttpWebJsonParser()
                )
            )
        )
    }
}

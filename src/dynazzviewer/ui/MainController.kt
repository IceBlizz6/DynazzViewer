package dynazzviewer.ui

import dynazzviewer.storage.Storage
import dynazzviewer.storage.sqlite.SqlLiteStorage
import dynazzviewer.ui.viewmodels.NodeViewModel
import dynazzviewer.ui.viewmodels.RootNodeViewModel
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

    init {
        storage = SqlLiteStorage(configuration)
    }
}

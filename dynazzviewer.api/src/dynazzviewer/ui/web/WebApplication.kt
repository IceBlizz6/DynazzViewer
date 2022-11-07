package dynazzviewer.ui.web

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dynazzviewer.controllers.AnimeSeasonController
import dynazzviewer.controllers.ServiceDescriptorController
import dynazzviewer.entities.AnimeSeasonFlagState
import dynazzviewer.entities.ExtDatabase
import dynazzviewer.files.FileCache
import dynazzviewer.files.FileConfiguration
import dynazzviewer.files.FileEntryFactory
import dynazzviewer.files.FileSystemRepository
import dynazzviewer.files.SystemFileSource
import dynazzviewer.services.HttpWebClient
import dynazzviewer.services.descriptors.jikan.JikanApi
import dynazzviewer.services.descriptors.jikan.MalType
import dynazzviewer.services.descriptors.jikan.MalYearSeason
import dynazzviewer.services.descriptors.tvmaze.TvMazeApi
import dynazzviewer.storage.MediaUnitSort
import dynazzviewer.storage.ReadOperation
import dynazzviewer.storage.SortOrder
import dynazzviewer.storage.Storage
import dynazzviewer.storage.sqlite.SqlLiteStorage
import dynazzviewer.ui.config.DefaultConfiguration
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.http.content.vue
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WebApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            System.setProperty("java.awt.headless", "false")
            val plugin = buildPlugin()
            val server = embeddedServer(Netty, 7111) {
                install(plugin) {
                    playground = true
                }
				routing {
					singlePageApplication {
						vue("static")
					}
				}
            }
            server.start(wait = true)
        }

        private fun buildPlugin(): GraphQLFeature {
            val settingsController = WebSettingsController()
            val configuration = DefaultConfiguration(settingsController)
            val storageMode = configuration.storageMode
            val storage = SqlLiteStorage(storageMode)
            val plugin = GraphQLFeature(
                storage
            ) {
                configure {
                    useDefaultPrettyPrinter = true
                    wrapErrors = false
                }
                enum<ExtDatabase>()
                enum<MalType>()
                enum<MalYearSeason>()
                enum<AnimeSeasonFlagState>()
                enum<MediaUnitSort>()
                enum<SortOrder>()
                this.stringScalar<LocalDate> {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    this.serialize = { it.format(formatter) }
                    this.deserialize = { LocalDate.parse(it, formatter) }
                }
                buildSchema(this, storage, configuration)
            }
            return plugin
        }

        private fun buildSchema(
            builder: SchemaBuilder,
            storage: Storage,
            configuration: FileConfiguration
        ) {
            val configPath: Path = Paths.get(
                DefaultConfiguration.userDirectory,
                DefaultConfiguration.configPropertiesFileName
            )
            val systemFileSource = SystemFileSource(configuration)
            val fileCache = FileCache(systemFileSource)
            val fileEntryFactory = FileEntryFactory(configuration, storage)
            val fileRepository = FileSystemRepository(fileCache, fileEntryFactory)
            val fileController = FileSystemController(
                storage = storage,
                fileConfiguration = configuration,
                fileRepository = fileRepository
            )
            val webClient = HttpWebClient(secondsThrottleDelay = 2)
            val jikanApi = JikanApi(
                webClient = webClient,
                fetchRelated = true,
                autoFillEpisodes = true,
                autoFillEpisodeAirDates = true
            )
            val tvMazeApi = TvMazeApi(
                webClient = webClient
            )
            val apiServiceController = ServiceDescriptorController(
                descriptorServices = listOf(jikanApi, tvMazeApi),
                listener = fileController,
                storage = storage
            )
            val animeSeasonController = AnimeSeasonController(
                storage = storage,
                api = jikanApi,
                config = configuration
            )

            builder.apply {
                FileSystemGraph(this, fileController)
                MediaListGraph(this, storage)
                DataManagementGraph(this, storage)
                ApiServiceGraph(this, apiServiceController, storage, animeSeasonController)
            }
        }

        fun Context.provider(): ContextProvider {
            return this.get()!!
        }

        fun Context.read(): ReadOperation {
            return this.provider().context
        }
    }
}

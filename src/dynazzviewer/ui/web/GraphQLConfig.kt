package dynazzviewer.ui.web

import dynazzviewer.controllers.AnimeSeasonController
import dynazzviewer.controllers.ServiceDescriptorController
import dynazzviewer.filesystem.FileCache
import dynazzviewer.filesystem.FileEntryFactory
import dynazzviewer.filesystem.FileSystemRepository
import dynazzviewer.filesystem.SystemFileSource
import dynazzviewer.services.HttpWebJsonParser
import dynazzviewer.services.descriptors.HttpThrottledWebParser
import dynazzviewer.services.descriptors.jikan.JikanApi
import dynazzviewer.services.descriptors.tvmaze.TvMazeApi
import dynazzviewer.storage.sqlite.SqlLiteStorage
import dynazzviewer.ui.config.DefaultConfiguration
import graphql.execution.ExecutionStrategy
import graphql.schema.GraphQLSchema
import io.leangen.graphql.GraphQLSchemaGenerator
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class GraphQLConfig {

    @Autowired
    private lateinit var applicationContext: ConfigurableApplicationContext

    @Bean
    open fun executionStrategies(): Map<String, ExecutionStrategy> {
        val executionStrategyMap = HashMap<String, ExecutionStrategy>()
        executionStrategyMap["queryExecutionStrategy"] = AsyncTransactionalExecutionStrategy()
        return executionStrategyMap
    }

    @Bean
    open fun graphQlSchema(): GraphQLSchema {
        val configPath: Path = Paths.get(
            DefaultConfiguration.userDirectory,
            DefaultConfiguration.configPropertiesFileName
        )
        val settingsController = WebSettingsController()
        val configuration = DefaultConfiguration(settingsController)
        val systemFileSource = SystemFileSource(configuration)
        val fileCache = FileCache(systemFileSource)
        val storage = SqlLiteStorage(configuration)
        val fileEntryFactory = FileEntryFactory(configuration, storage)
        val fileRepository = FileSystemRepository(fileCache, fileEntryFactory)
        val fileController = FileSystemController(
            storage = storage,
            fileConfiguration = configuration,
            userConfiguration = configuration,
            fileRepository = fileRepository
        )

        val jikanApi = JikanApi(
            parser = HttpThrottledWebParser(secondsInterval = 2),
            fetchRelated = true,
            autoFillEpisodes = true,
            autoFillEpisodeAirDates = true
        )

        val tvMazeApi = TvMazeApi(
            parser = HttpWebJsonParser()
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

        return GraphQLSchemaGenerator()
            .withOperationsFromSingleton(FileSystemGraph(fileController))
            .withOperationsFromSingleton(ConfigGraph(configuration))
            .withOperationsFromSingleton(MediaListGraph(storage))
            .withOperationsFromSingleton(
                ApiServiceGraph(apiServiceController, storage, animeSeasonController)
            )
            .withSchemaTransformers(GraphInferNullTransformer())
            .generate()
    }

    @Bean
    open fun crossOriginHeaderHandler(): CorsRequestFilter {
        return CorsRequestFilter(
            listOf(DefaultConfiguration.clientOrigin)
        )
    }
}

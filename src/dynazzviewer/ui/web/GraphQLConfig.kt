package dynazzviewer.ui.web

import dynazzviewer.filesystem.FileCache
import dynazzviewer.filesystem.FileEntryFactory
import dynazzviewer.filesystem.FileSystemRepository
import dynazzviewer.filesystem.SystemFileSource
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
        crossOriginHeaderHandler()

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

        return GraphQLSchemaGenerator()
            .withOperationsFromSingleton(FileSystemGraph(fileController))
            .withOperationsFromSingleton(ConfigGraph(configuration))
            .withOperationsFromSingleton(MediaListGraph(storage))
            .generate()
    }

    private fun crossOriginHeaderHandler() {
        val beanFactory = applicationContext.beanFactory
        val className = CorsRequestFilter::class.java.canonicalName
        val bean = CorsRequestFilter(DefaultConfiguration.clientOrigin)
        beanFactory.registerSingleton(className, bean)
    }
}

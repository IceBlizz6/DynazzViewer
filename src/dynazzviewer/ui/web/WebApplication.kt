package dynazzviewer.ui.web

import dynazzviewer.filesystem.FileCache
import dynazzviewer.filesystem.FileEntryFactory
import dynazzviewer.filesystem.FileSystemRepository
import dynazzviewer.filesystem.SystemFileSource
import dynazzviewer.storage.sqlite.SqlLiteStorage
import dynazzviewer.ui.config.DefaultConfiguration
import graphql.schema.GraphQLSchema
import io.leangen.graphql.GraphQLSchemaGenerator
import java.nio.file.Path
import java.nio.file.Paths
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
open class WebApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            System.setProperty("java.awt.headless", "false")
            SpringApplication.run(WebApplication::class.java, *args)
        }
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

        return GraphQLSchemaGenerator()
            .withOperationsFromSingleton(FileSystemGraph(fileController))
            .withOperationsFromSingleton(ConfigGraph(configuration))
            .generate()
    }
}

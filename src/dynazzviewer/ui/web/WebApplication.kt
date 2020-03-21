package dynazzviewer.ui.web

import graphql.schema.GraphQLSchema
import io.leangen.graphql.GraphQLSchemaGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
open class WebApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(WebApplication::class.java, *args)
        }
    }

    @Bean
    open fun graphQl(): GraphQLSchema {
        val schema = GraphQLSchemaGenerator()
            .withOperationsFromSingleton(GraphQlQuery())
            .generate()
        return schema
    }
}

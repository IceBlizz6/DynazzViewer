package dynazzviewer.ui.web

import com.apurebase.kgraphql.GraphQL
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dynazzviewer.storage.Storage
import io.ktor.server.application.Application
import io.ktor.server.application.Plugin
import io.ktor.util.AttributeKey

class GraphQLFeature(
    private val connection: Storage,
    private val block: SchemaBuilder.() -> Unit
) : Plugin<Application, GraphQL.Configuration, GraphQL> {
    override val key = AttributeKey<GraphQL>("KGraphQL")

    override fun install(
        pipeline: Application,
        configure: GraphQL.Configuration.() -> Unit
    ): GraphQL {
        val rootFeature = GraphQLFeatureInstance(connection, block)
        return rootFeature.install(pipeline, configure)
    }
}

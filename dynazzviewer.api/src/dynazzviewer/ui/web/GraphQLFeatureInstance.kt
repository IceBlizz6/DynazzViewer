package dynazzviewer.ui.web

import com.apurebase.kgraphql.GraphQL
import com.apurebase.kgraphql.GraphQLError
import com.apurebase.kgraphql.GraphqlRequest
import com.apurebase.kgraphql.KGraphQL
import com.apurebase.kgraphql.KtorGraphQLConfiguration
import com.apurebase.kgraphql.context
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dynazzviewer.storage.Storage
import dynazzviewer.ui.web.WebApplication.Companion.provider
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.charset
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.Plugin
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.application.pluginOrNull
import io.ktor.server.request.contentType
import io.ktor.server.request.receiveStream
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.util.AttributeKey
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json.Default.decodeFromString
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.nio.charset.Charset

class GraphQLFeatureInstance(
    private val connection: Storage,
    private val block: SchemaBuilder.() -> Unit
) : Plugin<Application, GraphQL.Configuration, GraphQL> {
    override val key = AttributeKey<GraphQL>("KGraphQL")

    override fun install(
        pipeline: Application,
        configure: GraphQL.Configuration.() -> Unit
    ): GraphQL {
        val config = GraphQL.Configuration().apply(configure)
        val schema = KGraphQL.schema {
            configuration = config
            block(this)
        }

        val routing: Routing.() -> Unit = {
            val routing: Route.() -> Unit = {
                route(config.endpoint) {
                    post {
                        val bodyAsText = call.receiveTextWithCorrectEncoding()
                        val request = decodeFromString(GraphqlRequest.serializer(), bodyAsText)
                        val ctx = context {
                            +ContextProvider(connection)
                        }
                        val result = schema.execute(
                            request.query,
                            request.variables.toString(),
                            ctx
                        )
                        ctx.provider().close()
                        call.respondText(result, contentType = ContentType.Application.Json)
                    }
                    if (config.playground) get {
                        @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                        val playgroundHtml = KtorGraphQLConfiguration::class
                            .java
                            .classLoader
                            .getResource(
                                "playground.html"
                            )
                            .readBytes()
                        call.respondBytes(playgroundHtml, contentType = ContentType.Text.Html)
                    }
                }
            }
            routing(this)
        }

        pipeline.pluginOrNull(Routing)?.apply(routing) ?: pipeline.install(Routing, routing)

        pipeline.intercept(ApplicationCallPipeline.Monitoring) {
            try {
                coroutineScope {
                    proceed()
                }
            } catch (e: Throwable) {
                if (e is GraphQLError) {
                    context.respond(HttpStatusCode.OK, e.serialize())
                } else throw e
            }
        }
        return GraphQL(schema)
    }

    private suspend fun ApplicationCall.receiveTextWithCorrectEncoding(): String {
        fun ContentType.defaultCharset(): Charset = when (this) {
            ContentType.Application.Json -> Charsets.UTF_8
            else -> Charsets.ISO_8859_1
        }

        val contentType = request.contentType()
        val suitableCharset = contentType.charset() ?: contentType.defaultCharset()
        return receiveStream().bufferedReader(charset = suitableCharset).readText()
    }

    private fun GraphQLError.serialize(): String = buildJsonObject {
        put(
            "errors",
            buildJsonArray {
                addJsonObject {
                    put("message", message)
                    put(
                        "locations",
                        buildJsonArray {
                            locations?.forEach {
                                addJsonObject {
                                    put("line", it.line)
                                    put("column", it.column)
                                }
                            }
                        }
                    )
                    put(
                        "path",
                        buildJsonArray {
                            // TODO: Build this path. https://spec.graphql.org/June2018/#example-90475
                        }
                    )
                }
            }
        )
    }.toString()
}

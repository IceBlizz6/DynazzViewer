package dynazzviewer.ui.web

import dynazzviewer.entities.MediaUnit
import dynazzviewer.storage.Storage
import io.leangen.graphql.annotations.GraphQLEnvironment
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.execution.ResolutionEnvironment

class MediaListGraph(
    val storage: Storage
) {
    @GraphQLQuery
    fun listMediaUnits(@GraphQLEnvironment env: ResolutionEnvironment): List<MediaUnit> {
        val operation = ContextHandler.registerRead(storage, env)
        return operation.mediaUnits()
    }
}

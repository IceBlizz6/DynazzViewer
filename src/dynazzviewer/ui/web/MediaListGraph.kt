package dynazzviewer.ui.web

import dynazzviewer.base.ViewStatus
import dynazzviewer.entities.MediaPartCollection
import dynazzviewer.entities.MediaUnit
import dynazzviewer.storage.Storage
import io.leangen.graphql.annotations.GraphQLEnvironment
import io.leangen.graphql.annotations.GraphQLMutation
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

    @GraphQLQuery
    fun internalMediaSearch(@GraphQLEnvironment env: ResolutionEnvironment, name: String): List<MediaPartCollection> {
        val operation = ContextHandler.registerRead(storage, env)
        return operation.mediaPartCollectionsLike(name)
    }

    @GraphQLMutation
    fun setEpisodeWatchState(mediaPartId: Int, status: ViewStatus) {
        storage.readWrite().use { context ->
            val mediaPart = context.mediaPartById(mediaPartId)
            mediaPart.status = status
        }
    }
}

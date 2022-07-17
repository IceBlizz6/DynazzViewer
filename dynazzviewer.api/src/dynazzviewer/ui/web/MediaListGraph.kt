package dynazzviewer.ui.web

import dynazzviewer.entities.ExtDatabase
import dynazzviewer.entities.MediaPartCollection
import dynazzviewer.entities.MediaUnit
import dynazzviewer.entities.ViewStatus
import dynazzviewer.storage.MediaUnitSort
import dynazzviewer.storage.SortOrder
import dynazzviewer.storage.Storage
import io.leangen.graphql.annotations.GraphQLEnvironment
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.execution.ResolutionEnvironment

class MediaListGraph(
    val storage: Storage
) {
    @GraphQLQuery
    fun listMediaUnits(
        @GraphQLEnvironment env: ResolutionEnvironment,
        skip: Int,
        take: Int,
        sort: MediaUnitSort,
        order: SortOrder
    ): List<MediaUnit> {
        val operation = ContextHandler.registerRead(storage, env)
        return operation.mediaUnits(skip, take, sort, order)
    }

    @GraphQLQuery
    fun internalMediaSearch(
        @GraphQLEnvironment env: ResolutionEnvironment,
        name: String
    ): List<MediaPartCollection> {
        val operation = ContextHandler.registerRead(storage, env)
        return operation.mediaPartCollectionsLike(name)
    }

    @GraphQLQuery
    fun internalMediaLookup(
        @GraphQLEnvironment env: ResolutionEnvironment,
        db: ExtDatabase,
        code: String
    ): MediaPartCollection? {
        val operation = ContextHandler.registerRead(storage, env)
        return operation.mediaPartCollectionByCode(db, code)
    }

    @GraphQLMutation
    fun setEpisodeWatchState(mediaPartId: Int, status: ViewStatus) {
        storage.readWrite { context ->
            val mediaPart = context.mediaPartById(mediaPartId)
            mediaPart.status = status
            mediaPart.mediaFile?.let {
                it.status = status
            }
        }
    }
}

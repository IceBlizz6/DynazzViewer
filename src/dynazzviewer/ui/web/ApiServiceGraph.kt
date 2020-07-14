package dynazzviewer.ui.web

import dynazzviewer.base.ExtDatabase
import dynazzviewer.controllers.ServiceDescriptorController
import dynazzviewer.services.descriptors.DescriptionUnit
import dynazzviewer.services.descriptors.ResultHeader
import dynazzviewer.storage.Storage
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery

class ApiServiceGraph(
    val service: ServiceDescriptorController,
    val storage: Storage
) {
    @GraphQLQuery
    fun externalMediaSearch(db: ExtDatabase, name: String): List<MediaSearchResultItem> {
        val result: List<ResultHeader> = service.queryDescriptors(db, name)
        storage.read().use { context ->
            val storedState = context.mediaUnitExist(result)
            return result.map {
                MediaSearchResultItem(
                    name = it.name,
                    saved = storedState[it]!!,
                    extDb = it.extDb,
                    extDbCode = it.extDbCode,
                    imageUrl = it.imageUrl
                )
            }
        }
    }

    @GraphQLQuery
    fun externalMediaLookup(db: ExtDatabase, code: String): DescriptionUnit? {
        return service.queryDescriptor(db, code)
    }

    @GraphQLMutation
    fun externalMediaAdd(db: ExtDatabase, code: String): Boolean {
        val description = service.queryDescriptor(db, code)
        if (description == null) {
            return false
        } else {
            service.add(description)
            return true
        }
    }

    class MediaSearchResultItem(
        val name: String,
        val imageUrl: String,
        val extDb: ExtDatabase,
        val extDbCode: String,
        val saved: Boolean
    )
}

package dynazzviewer.ui.web

import dynazzviewer.base.ExtDatabase
import dynazzviewer.controllers.ServiceDescriptorController
import dynazzviewer.services.descriptors.DescriptionUnit
import dynazzviewer.services.descriptors.ResultHeader
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery

class ApiServiceGraph(
    val service: ServiceDescriptorController
) {
    @GraphQLQuery
    fun externalMediaSearch(db: ExtDatabase, name: String): List<ResultHeader> {
        return service.queryDescriptors(db, name)
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
}

package dynazzviewer.ui.web

import dynazzviewer.entities.MediaFile
import dynazzviewer.entities.MediaUnit
import io.leangen.graphql.annotations.GraphQLEnvironment
import io.leangen.graphql.annotations.GraphQLQuery

class GraphQlQuery {
    @GraphQLQuery
    fun getMediaUnit(id: Long?, text: String?): MediaUnit? {
        return null;
    }
}

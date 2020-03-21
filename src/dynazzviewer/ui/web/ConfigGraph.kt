package dynazzviewer.ui.web

import dynazzviewer.ui.config.DefaultConfiguration
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery

class ConfigGraph(
    val configuration: DefaultConfiguration
) {
    @GraphQLQuery
    fun mediaPlayerApplicationPath(): String? {
        return configuration.mediaPlayerApplicationPath
    }

    @GraphQLMutation
    fun mediaPlayerApplicationPath(path: String) {
        configuration.mediaPlayerApplicationPath = path
    }
}

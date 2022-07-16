package dynazzviewer.ui.web

import dynazzviewer.storage.Storage
import io.leangen.graphql.annotations.GraphQLMutation
import java.io.File

class DataManagementGraph(
    private val storage: Storage
) {
    @GraphQLMutation
    fun dumpDatabaseToJsonFile(filePath: String) {
        val file = File(filePath)
        storage.dumpTo(file)
    }
}

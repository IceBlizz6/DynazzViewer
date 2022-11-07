package dynazzviewer.ui.web

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dynazzviewer.storage.Storage
import java.io.File

class DataManagementGraph(
    builder: SchemaBuilder,
    private val storage: Storage
) {
    init {
        builder.mutation("dumpDatabaseToJsonFile") {
            resolver { filePath: String ->
                val file = File(filePath)
                storage.dumpTo(file)
                true
            }
        }
    }
}

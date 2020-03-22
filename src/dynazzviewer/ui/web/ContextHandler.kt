package dynazzviewer.ui.web

import dynazzviewer.storage.ReadOperation
import dynazzviewer.storage.Storage
import io.leangen.graphql.execution.ResolutionEnvironment

class ContextHandler {
    companion object {
        private val operationList = mutableMapOf<String, ReadOperation>()

        fun registerRead(storage: Storage, env: ResolutionEnvironment): ReadOperation {
            val executionId = env.dataFetchingEnvironment.executionId.toString()
            val operation = storage.read()
            operationList[executionId] = operation
            return operation
        }

        fun clearIfPresent(executionId: String) {
            if (operationList.containsKey(executionId)) {
                operationList[executionId]!!.close()
                operationList.remove(executionId)
            }
        }
    }
}

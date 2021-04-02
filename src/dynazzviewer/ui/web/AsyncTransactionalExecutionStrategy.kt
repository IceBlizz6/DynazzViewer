package dynazzviewer.ui.web

import graphql.ExecutionResult
import graphql.execution.AsyncExecutionStrategy
import graphql.execution.ExecutionContext
import graphql.execution.ExecutionStrategyParameters
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

@Service
class AsyncTransactionalExecutionStrategy : AsyncExecutionStrategy() {
    companion object {
        private val callMap = mutableMapOf<String, Int>()
    }

    override fun execute(
        executionContext: ExecutionContext?,
        parameters: ExecutionStrategyParameters?
    ): CompletableFuture<ExecutionResult> {
        val executionId = executionContext!!.executionId.toString()
        if (callMap.containsKey(executionId)) {
            callMap[executionId] = callMap[executionId]!! + 1
        } else {
            callMap[executionId] = 1
        }
        val result = super.execute(executionContext, parameters)

        callMap[executionId] = callMap[executionId]!! - 1
        if (callMap[executionId]!! == 0) {
            ContextHandler.clearIfPresent(executionId)
        }
        return result
    }
}

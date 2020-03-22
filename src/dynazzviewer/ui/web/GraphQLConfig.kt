package dynazzviewer.ui.web

import graphql.execution.ExecutionStrategy
import java.util.HashMap
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class GraphQLConfig {
    @Bean
    open fun executionStrategies(): Map<String, ExecutionStrategy> {
        val executionStrategyMap = HashMap<String, ExecutionStrategy>()
        executionStrategyMap["queryExecutionStrategy"] = AsyncTransactionalExecutionStrategy()
        return executionStrategyMap
    }
}

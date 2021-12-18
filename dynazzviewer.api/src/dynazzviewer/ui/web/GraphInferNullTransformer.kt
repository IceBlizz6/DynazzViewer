package dynazzviewer.ui.web

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import io.leangen.graphql.generator.BuildContext
import io.leangen.graphql.generator.OperationMapper
import io.leangen.graphql.generator.mapping.SchemaTransformer
import io.leangen.graphql.metadata.Operation
import java.lang.reflect.Field
import kotlin.reflect.KType
import kotlin.reflect.jvm.kotlinProperty

class GraphInferNullTransformer : SchemaTransformer {
    override fun transformField(
        field: GraphQLFieldDefinition,
        operation: Operation,
        operationMapper: OperationMapper,
        buildContext: BuildContext
    ): GraphQLFieldDefinition {
        return field.transform { builder: GraphQLFieldDefinition.Builder ->
            if (field.type is GraphQLNonNull) {
                field.type
            } else {
                val returnType: KType? = operation
                    .typedElement
                    .elements
                    .filterIsInstance(Field::class.java)
                    .singleOrNull()
                    ?.kotlinProperty
                    ?.getter
                    ?.returnType

                var graphType = field.type
                if (graphType is GraphQLList) {
                    val projectedType = returnType?.arguments?.single()
                    val typeArgIsNullable = projectedType?.type?.isMarkedNullable
                    if (typeArgIsNullable != true) {
                        graphType = GraphQLList(GraphQLNonNull(graphType.wrappedType))
                    }
                }

                if (returnType?.isMarkedNullable == true) {
                    graphType
                } else {
                    builder.type(GraphQLNonNull(graphType))
                }
            }
        }
    }
}

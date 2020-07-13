package dynazzviewer.entities.plugins

import io.leangen.graphql.annotations.GraphQLNonNull
import javax.persistence.Entity
import net.bytebuddy.build.Plugin
import net.bytebuddy.description.annotation.AnnotationDescription
import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.implementation.SuperMethodCall
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers.isGetter

class GraphNonNullPlugin : Plugin {
    /**
     * Only affect Entity classes
     */
    override fun matches(target: TypeDescription): Boolean {
        return target.declaredAnnotations.isAnnotationPresent(Entity::class.java) ||
            target.declaredAnnotations.isAnnotationPresent(InferGraphQlNullity::class.java)
    }

    /**
     * Annotate all getter methods in Entity classes with @GraphQLNonNull if
     *      they are already annotated with jetbrains's @NotNull
     */
    override fun apply(
        builder: DynamicType.Builder<*>,
        typeDescription: TypeDescription,
        classFileLocator: ClassFileLocator
    ): DynamicType.Builder<*> {
        val annotation = AnnotationDescription.Builder.ofType(GraphQLNonNull::class.java).build()
        return builder
            .method(isGetter<MethodDescription>()
                .and(isNotNullAnnotated())
                .and(isNotGraphQlNullAnnotated()))
            .intercept(SuperMethodCall.INSTANCE)
            .annotateMethod(annotation)
    }

    override fun close() {}

    /**
     * Method has a @org.jetbrains.annotations.NotNull annotation
     * This annotation is missing for primitive types that can not be null, but those are covered by code in GraphQL already.
     */
    private fun isNotNullAnnotated(): ElementMatcher<MethodDescription> {
        return ElementMatcher {
            it.declaredAnnotations
                .map { it.annotationType.toString() }
                .any { it == "interface org.jetbrains.annotations.NotNull" }
        }
    }

    private fun isNotGraphQlNullAnnotated(): ElementMatcher<MethodDescription> {
        return ElementMatcher {
            it.declaredAnnotations
                .map { it.annotationType.toString() }
                .none { it == "interface io.leangen.graphql.annotations.GraphQLNonNull" }
        }
    }
}

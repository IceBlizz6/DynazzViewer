package dynazzviewer.entities.plugins

import net.bytebuddy.ByteBuddy
import net.bytebuddy.ClassFileVersion
import net.bytebuddy.build.EntryPoint
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.dynamic.scaffold.TypeValidation
import net.bytebuddy.dynamic.scaffold.inline.MethodNameTransformer

class GraphNonNullEntryPoint : EntryPoint {
    override fun byteBuddy(classFileVersion: ClassFileVersion?): ByteBuddy? {
        return ByteBuddy(classFileVersion)
            .with(TypeValidation.DISABLED)
    }

    override fun transform(
        typeDescription: TypeDescription?,
        byteBuddy: ByteBuddy,
        classFileLocator: ClassFileLocator?,
        methodNameTransformer: MethodNameTransformer?
    ): DynamicType.Builder<*>? {
        return byteBuddy.rebase<Any>(typeDescription, classFileLocator, methodNameTransformer)
    }
}

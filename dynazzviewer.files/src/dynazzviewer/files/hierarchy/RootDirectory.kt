package dynazzviewer.files.hierarchy

import java.io.File

class RootDirectory(
    rootPath: String
) {
    val rootPath: String

    init {
        val file = File(rootPath)
        val temp = file.path
        if (!file.isAbsolute) {
            throw InvalidPathException("Path not rooted: $rootPath")
        } else {
            this.rootPath = rootPath
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is RootDirectory) {
            return this.rootPath == other.rootPath
        } else {
            return super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return rootPath.hashCode()
    }
}

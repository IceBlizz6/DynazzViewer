package dynazzviewer.filesystem.hierarchy

import java.io.File

class FilePath(
    path: String
) {
    val path: String

    init {
        val file = File(path)
        if (file.isAbsolute) {
            this.path = path
        } else {
            throw InvalidPathException("Path is not rooted: $path")
        }
    }

    val fileName: FileName
        get() { return FileName(File(path).name) }

    override fun equals(other: Any?): Boolean {
        if (other is FilePath) {
            return this.path == other.path
        } else {
            return super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }
}

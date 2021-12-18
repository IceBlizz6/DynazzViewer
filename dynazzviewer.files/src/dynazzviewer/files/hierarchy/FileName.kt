package dynazzviewer.files.hierarchy

import java.io.File

class FileName(
    name: String
) {
    val name: String

    init {
        if (name.contains(java.io.File.separatorChar)) {
            throw InvalidPathException("File name cannot contain separators")
        } else {
            this.name = name
        }
    }

    val extension: String
        get() {
            return File(name).extension
        }

    override fun equals(other: Any?): Boolean {
        if (other is FileName) {
            return this.name == other.name
        } else {
            return super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

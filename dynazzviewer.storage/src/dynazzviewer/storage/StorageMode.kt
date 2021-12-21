package dynazzviewer.storage

sealed class StorageMode {

    abstract val path: String

    abstract val initOperation: DataInitOp

    object Memory : StorageMode() {
        override val path: String
            get() = ":memory:"

        override val initOperation: DataInitOp
            get() = DataInitOp.CREATE_DROP
    }

    class File(
        val rootPath: String
    ) : StorageMode() {
        companion object {
            const val DB_FILENAME = "media.db"
        }

        override val path: String
            get() = rootPath + java.io.File.separatorChar + DB_FILENAME

        override val initOperation: DataInitOp
            get() = DataInitOp.UPDATE
    }

    enum class DataInitOp(val opName: String) {
        UPDATE("update"),
        CREATE_DROP("create-drop")
    }
}

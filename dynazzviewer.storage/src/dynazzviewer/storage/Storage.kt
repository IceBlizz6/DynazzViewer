package dynazzviewer.storage

import java.io.File

interface Storage {
    fun readKeepAlive(): ReadOperation

    fun <T> read(use: (ReadOperation) -> T): T

    fun <T> readWrite(use: (ReadWriteOperation) -> T): T

    fun dumpTo(file: File)
}

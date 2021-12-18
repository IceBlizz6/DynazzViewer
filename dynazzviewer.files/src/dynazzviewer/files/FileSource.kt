package dynazzviewer.files

interface FileSource {
    fun listFiles(path: String): Set<String>

    fun readCacheFile(path: String): Set<String>

    fun isCached(path: String): Boolean

    fun saveCacheFile(path: String, content: Set<String>)
}

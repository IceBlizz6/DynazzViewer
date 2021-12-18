package dynazzviewer.filesystem

import dynazzviewer.filesystem.hierarchy.FileName
import dynazzviewer.filesystem.hierarchy.FilePath
import dynazzviewer.filesystem.hierarchy.RootDirectory

class FileCache(
    private val source: FileSource
) {
    private val nameToFilePaths: MutableMap<FileName, MutableSet<FilePath>> = mutableMapOf()

    private val rootToFilePaths: MutableMap<RootDirectory, MutableSet<FilePath>> = mutableMapOf()

    fun readRoot(root: RootDirectory): Set<FilePath> {
        val paths: Set<String>
        if (source.isCached(root.rootPath)) {
            val rootCache = source.readCacheFile(root.rootPath)
            val filePaths = rootCache.map { e -> FilePath(e) }.toSet()
            cacheRoot(root, filePaths)
            return filePaths
        } else {
            paths = source.listFiles(root.rootPath)
            val filePaths = paths.map { e -> FilePath(e) }.toSet()
            save(root, filePaths)
            return filePaths
        }
    }

    fun readSubDirectory(subDirectory: String): Set<FilePath> {
        return source.listFiles(subDirectory).map { e -> FilePath(e) }.toSet()
    }

    private fun save(root: RootDirectory, filePaths: Set<FilePath>) {
        cacheRoot(root, filePaths)
        source.saveCacheFile(root.rootPath, filePaths.map { e -> e.path }.toSet())
    }

    private fun cacheRoot(root: RootDirectory, filePaths: Set<FilePath>) {
        rootToFilePaths[root] = filePaths.toMutableSet()
        updateNameCache(filePaths)
    }

    fun update(root: RootDirectory, subDirectory: String, filePaths: Set<FilePath>) {
        val paths: MutableSet<FilePath> = rootToFilePaths[root]!!
        paths.removeAll { e -> e.path.startsWith(subDirectory) }
        paths.addAll(filePaths)
        save(root, paths)
    }

    fun filePathsByName(fileName: FileName): Set<FilePath> {
        val value = nameToFilePaths[fileName]
        if (value == null) {
            return setOf()
        } else {
            return value
        }
    }

    private fun updateNameCache(added: Set<FilePath>) {
        val grouped = added.groupBy { e -> e.fileName }
        for (group in grouped) {
            var cacheEntry = nameToFilePaths[group.key]
            if (cacheEntry == null) {
                val filePathSet = mutableSetOf<FilePath>()
                nameToFilePaths[group.key] = filePathSet
                cacheEntry = filePathSet
            }
            cacheEntry.addAll(group.value)
        }
    }
}

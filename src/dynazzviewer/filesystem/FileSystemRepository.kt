package dynazzviewer.filesystem

import dynazzviewer.filesystem.hierarchy.FileName
import dynazzviewer.filesystem.hierarchy.FilePath
import dynazzviewer.filesystem.hierarchy.RootDirectory
import dynazzviewer.services.filesystem.VideoFile

class FileSystemRepository(
    private val cache: FileCache,
    private val factory: FileEntryFactory
) : FileRepository {
    private val roots = mutableListOf<RootDirectory>()

    override fun add(rootPath: String): Set<VideoFile> {
        val root = RootDirectory(rootPath)
        val filePaths = cache.readRoot(root)
        roots.add(root)
        return factory.files(root, filePaths)
    }

    override fun list(): Map<String, Set<VideoFile>> {
        val map = mutableMapOf<String, Set<VideoFile>>()
        for (root in roots) {
            val filePaths = cache.readRoot(root)
            map.put(root.rootPath, factory.files(root, filePaths))
        }
        return map
    }

    override fun remove(rootPath: String): Boolean {
        val root = roots.singleOrNull { e -> e.rootPath == rootPath }
        if (root == null) {
            return false
        } else {
            return roots.remove(root)
        }
    }

    override fun refreshDirectory(directoryPath: String): Map<RootDirectory, Set<VideoFile>> {
        val filePaths = cache.readSubDirectory(directoryPath)
        for (root in roots) {
            if (directoryPath.startsWith(root.rootPath)) {
                cache.update(root, directoryPath, filePaths)
            }
        }
        return update(filePaths)
    }

    override fun updateFileName(fileName: FileName): Map<RootDirectory, Set<VideoFile>> {
        val filePaths = cache.filePathsByName(fileName)
        return update(filePaths)
    }

    private fun update(filePaths: Set<FilePath>): Map<RootDirectory, Set<VideoFile>> {
        return roots
            .map { e ->
                e to
                    filePaths.filter { filePath -> filePath.path.startsWith(e.rootPath) }.toSet()
            }
            .map { e -> e.first to factory.files(e.first, e.second).toSet() }
            .toMap()
    }
}

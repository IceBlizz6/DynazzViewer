package filesystem

import dynazzviewer.filesystem.FileCache
import dynazzviewer.filesystem.FileSource
import dynazzviewer.filesystem.hierarchy.FileName
import dynazzviewer.filesystem.hierarchy.FilePath
import dynazzviewer.filesystem.hierarchy.RootDirectory
import org.junit.Assert
import org.junit.Test

class FileCacheTest {
    @Test
    fun isCachedTest() {
        val source = MockFileSource()
        val cache = FileCache(source)
        source.addFiles(setOf(
            "O:/test/file1.mkv",
            "O:/test/file2.mkv"
        ))
        cache.readRoot(RootDirectory("O:/test"))
        Assert.assertEquals(1, source.cacheFiles.keys.count())
        val key = source.cacheFiles.keys.single()
        Assert.assertEquals(2, source.cacheFiles[key]!!.count())
    }

    @Test
    fun pathsByNameTest() {
        val source = MockFileSource()
        val cache = FileCache(source)
        source.addFiles(setOf(
            "O:/test/file1.mkv",
            "O:/test/file2.mkv",
            "O:/test/test2/file1.mkv"
        ))
        cache.readRoot(RootDirectory("O:/test"))
        val paths = cache.filePathsByName(FileName("file1.mkv"))
        Assert.assertEquals(2, paths.count())
        Assert.assertTrue(paths.any { e -> e.path == "O:/test/file1.mkv" })
        Assert.assertTrue(paths.any { e -> e.path == "O:/test/test2/file1.mkv" })
    }

    @Test
    fun readSubDirectoryTest() {
        val source = MockFileSource()
        val cache = FileCache(source)
        source.addFiles(setOf(
            "O:/test/file1.mkv",
            "O:/test/file2.mkv",
            "O:/test/test2/file1.mkv"
        ))
        cache.readRoot(RootDirectory("O:/test"))
        val filePaths = cache.readSubDirectory("O:/test/test2")
        Assert.assertEquals(1, filePaths.count())
        Assert.assertEquals("O:/test/test2/file1.mkv", filePaths.single().path)
    }

    @Test
    fun updateTest() {
        val source = MockFileSource()
        val cache = FileCache(source)
        source.addFiles(setOf(
            "O:/test/file1.mkv",
            "O:/test/file2.mkv",
            "O:/test/test2/file1.mkv"
        ))
        cache.readRoot(RootDirectory("O:/test"))
        source.removeFiles(setOf(
            "O:/test/test2/file1.mkv"
        ))
        source.addFiles(setOf(
            "O:/test/test2/file2.mkv"
        ))
        cache.update(RootDirectory("O:/test"), "O:/test/test2", setOf(
            FilePath("O:/test/test2/file2.mkv")
        ))
        val cached = source.cacheFiles["O:/test"]!!
        Assert.assertEquals(3, cached.count())
        Assert.assertTrue(cached.any { e -> e == "O:/test/test2/file2.mkv" })
        Assert.assertFalse(cached.any { e -> e == "O:/test/test2/file1.mkv" })
    }

    class MockFileSource : FileSource {
        val cacheFiles = HashMap<String, Set<String>>()
        private val files = mutableListOf<String>()

        fun addFiles(added: Set<String>) {
            files.addAll(added)
        }

        fun removeFiles(removed: Set<String>) {
            files.removeAll(removed)
        }

        override fun listFiles(path: String): Set<String> {
            return files.filter { e -> e.startsWith(path) }.toSet()
        }

        override fun readCacheFile(path: String): Set<String> {
            return cacheFiles[path]!!
        }

        override fun isCached(path: String): Boolean {
            return cacheFiles.containsKey(path)
        }

        override fun saveCacheFile(path: String, content: Set<String>) {
            cacheFiles[path] = content
        }
    }
}

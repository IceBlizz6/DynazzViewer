package filesystem

import dynazzviewer.base.Configuration
import dynazzviewer.base.ViewStatus
import dynazzviewer.entities.MediaFile
import dynazzviewer.filesystem.FileCache
import dynazzviewer.filesystem.FileConfiguration
import dynazzviewer.filesystem.FileEntryFactory
import dynazzviewer.filesystem.FileSource
import dynazzviewer.filesystem.FileSystemRepository
import dynazzviewer.filesystem.hierarchy.FileName
import dynazzviewer.filesystem.hierarchy.InvalidPathException
import dynazzviewer.storage.Storage
import dynazzviewer.storage.StorageMode
import dynazzviewer.storage.sqlite.SqlLiteStorage
import org.junit.Assert
import org.junit.Test
import java.lang.Exception
import java.lang.RuntimeException

class FileRepositoryTest {
    private val storage: Storage
    private val source: MockFileSource
    private val repository: FileSystemRepository

    init {
        val configuration = MockFileConfiguration()
        source = MockFileSource()
        storage = SqlLiteStorage(configuration)
        repository = FileSystemRepository(
            cache = FileCache(source),
            factory = FileEntryFactory(
                storage = storage,
                configuration = configuration
            )
        )
    }

    @Test
    fun addNonRootedDirectory() {
        try {
            repository.add("myfolder/mysubfolder/")
            Assert.fail()
        } catch (expected: InvalidPathException) {}
    }

    @Test
    fun addTest() {
        source.addFiles(
            setOf(
                "O:/series/subfolder/file.mkv"
            )
        )
        val videoFiles = repository.add("O:/series")
        Assert.assertEquals(1, videoFiles.count())
    }

    @Test
    fun addIgnoreOtherFilesTest() {
        source.addFiles(
            setOf(
                "O:/series/subfolder/file.mkv",
                "O:/series/subfolder/file2.mkv",
                "O:/series/subfolder/file.sub"
            )
        )
        val videoFiles = repository.add("O:/series")
        Assert.assertEquals(2, videoFiles.count())
    }

    @Test
    fun addViewedFileTest() {
        storage.readWrite().use { context ->
            context.save(
                MediaFile(
                    name = "file.mkv",
                    status = ViewStatus.Viewed
                )
            )
        }
        source.addFiles(
            setOf(
                "O:/series/subfolder/file.mkv"
            )
        )
        val videoFiles = repository.add("O:/series")
        Assert.assertEquals(1, videoFiles.count())
        Assert.assertEquals(ViewStatus.Viewed, videoFiles.single().viewStatus)
    }

    @Test
    fun updateFileTest() {
        source.addFiles(
            setOf(
                "O:/series/subfolder/file.mkv"
            )
        )
        val oldFiles = repository.add("O:/series")
        Assert.assertEquals(null, oldFiles.single().mediaFileId)
        storage.readWrite().use { context ->
            context.save(
                MediaFile(
                    name = "file.mkv",
                    status = ViewStatus.Viewed
                )
            )
        }
        val map = repository.updateFileName(FileName(("file.mkv")))
        Assert.assertEquals(1, map.keys.count())
        val key = map.keys.single()
        Assert.assertEquals(1, map[key]!!.count())
        Assert.assertEquals(1, map[key]!!.single().mediaFileId)
    }

    @Test
    fun updateOverlappingRoots() {
        source.addFiles(
            setOf(
                "O:/series/subfolder/file.mkv"
            )
        )
        repository.add("O:/series")
        repository.add("O:/series/subfolder")
        storage.readWrite().use { context ->
            context.save(
                MediaFile(
                    name = "file.mkv",
                    status = ViewStatus.Viewed
                )
            )
        }
        val map = repository.updateFileName(FileName(("file.mkv")))
        Assert.assertEquals(2, map.keys.count())
    }

    class MockFileSource : FileSource {
        private val files: MutableSet<String> = mutableSetOf()

        fun addFiles(addedFiles: Set<String>) {
            files.addAll(addedFiles)
        }

        override fun listFiles(path: String): Set<String> {
            return files
        }

        override fun readCacheFile(path: String): Set<String> {
            throw Exception()
        }

        override fun isCached(path: String): Boolean {
            return false
        }

        override fun saveCacheFile(path: String, content: Set<String>) = Unit
    }

    class MockFileConfiguration :
        FileConfiguration,
        Configuration {
        override var rootDirectoryPaths: Set<String>
            get() = throw RuntimeException()
            set(value) = throw NotImplementedError()

        override val cacheDirectoryPath: String
            get() = throw RuntimeException()

        override val videoExtensions: Set<String>
            get() = setOf("mkv")

        override val extensionFilter: Set<String>
            get() = setOf("mkv")

        override val rootStorageDirectory: String
            get() = throw RuntimeException()

        override val storageMode: StorageMode
            get() = StorageMode.MEMORY
    }
}

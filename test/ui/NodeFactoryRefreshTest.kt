package ui

import base.TestConfiguration
import dynazzviewer.filesystem.FileCache
import dynazzviewer.filesystem.FileConfiguration
import dynazzviewer.filesystem.FileEntryFactory
import dynazzviewer.filesystem.FileSource
import dynazzviewer.filesystem.FileSystemRepository
import dynazzviewer.filesystem.hierarchy.FilePath
import dynazzviewer.storage.sqlite.SqlLiteStorage
import dynazzviewer.ui.viewmodels.NodeFactory
import dynazzviewer.ui.viewmodels.RootNodeViewModel
import java.io.File
import javax.transaction.NotSupportedException
import org.junit.Assert
import org.junit.Before
import org.junit.Test

public class NodeFactoryRefreshTest {
    private val fileSystemRoot: RootNodeViewModel = RootNodeViewModel()
    private val fileSource = MockFileSource()
    private lateinit var nodeFactory: NodeFactory

    @Before
    fun setup() {
        val configuration = TestConfiguration()
        val fileRepository = FileSystemRepository(
            cache = FileCache(fileSource),
            factory = FileEntryFactory(MockFileConfiguration(), SqlLiteStorage(configuration))
        )
        this.nodeFactory = NodeFactory(fileRepository)
    }

    @Test
    fun refreshExchangeTest() {
        fileSource.mockFileList = mutableSetOf(
            "H:${File.separatorChar}downloads${File.separatorChar}file1.mkv",
            "H:${File.separatorChar}downloads${File.separatorChar}file2.mkv"
        )
        val root = nodeFactory.createRootViewModel("H:${File.separatorChar}downloads")
        fileSystemRoot.children.add(root)
        fileSource.mockFileList = mutableSetOf(
            "H:${File.separatorChar}downloads${File.separatorChar}file1.mkv",
            "H:${File.separatorChar}downloads${File.separatorChar}file3.mkv"
        )
        nodeFactory.refreshDirectory(fileSystemRoot, FilePath(root.fullPath))
        Assert.assertEquals(2, root.children.count())
        Assert.assertTrue(
            root
                .children
                .any { it.fullPath ==
                        "H:${File.separatorChar}downloads${File.separatorChar}file1.mkv"
                }
        )
        Assert.assertTrue(
            root
                .children
                .any { it.fullPath ==
                        "H:${File.separatorChar}downloads${File.separatorChar}file3.mkv"
                }
        )
    }

    @Test
    fun refreshDiscardSyncRootInnerTest() {
        fileSource.mockFileList = mutableSetOf(
            "H:${File.separatorChar}Dir1${File.separatorChar}file1.mkv"
        )
        val root1 = nodeFactory.createRootViewModel("H:${File.separatorChar}")
        val root2 = nodeFactory.createRootViewModel("H:${File.separatorChar}Dir1")
        fileSystemRoot.children.add(root1)
        fileSystemRoot.children.add(root2)
        Assert.assertEquals(1, root2.children.count())
        fileSource.mockFileList = mutableSetOf(
            "H:${File.separatorChar}Dir1${File.separatorChar}file2.mkv",
            "H:${File.separatorChar}Dir1${File.separatorChar}file3.mkv"
        )
        nodeFactory.refreshDirectory(fileSystemRoot, FilePath("H:${File.separatorChar}"))
        Assert.assertEquals(1, root1.children.count())
        Assert.assertEquals(2, root2.children.count())
    }

    @Test
    fun refreshDiscardSyncRootOuterTest() {
        fileSource.mockFileList = mutableSetOf(
            "H:${File.separatorChar}Dir1${File.separatorChar}file1.mkv"
        )
        val root1 = nodeFactory.createRootViewModel("H:${File.separatorChar}")
        val root2 = nodeFactory.createRootViewModel("H:${File.separatorChar}Dir1")
        fileSystemRoot.children.add(root1)
        fileSystemRoot.children.add(root2)
        Assert.assertEquals(1, root2.children.count())
        fileSource.mockFileList = mutableSetOf(
            "H:${File.separatorChar}Dir1${File.separatorChar}file2.mkv",
            "H:${File.separatorChar}Dir1${File.separatorChar}file3.mkv"
        )
        nodeFactory.refreshDirectory(fileSystemRoot, FilePath("H:${File.separatorChar}Dir1"))
        Assert.assertEquals(1, root1.children.count())
        Assert.assertEquals(2, root2.children.count())
    }

    class MockFileConfiguration : FileConfiguration {
        override val videoExtensions: Set<String>
            get() = setOf("mkv")
        override val extensionFilter: Set<String>
            get() = setOf("mkv")
        override var rootDirectoryPaths: Set<String>
            get() = setOf()
            set(value) {}
        override val cacheDirectoryPath: String
            get() = throw NotSupportedException()
    }

    class MockFileSource : FileSource {
        var mockFileList: MutableSet<String> = mutableSetOf()

        override fun listFiles(path: String): Set<String> {
            return mockFileList
        }

        override fun readCacheFile(path: String): Set<String> = throw NotSupportedException()

        override fun isCached(path: String): Boolean = false

        override fun saveCacheFile(path: String, content: Set<String>) = Unit
    }
}

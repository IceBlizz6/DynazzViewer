package ui

import dynazzviewer.base.ViewStatus
import dynazzviewer.filesystem.FileRepository
import dynazzviewer.filesystem.hierarchy.FileName
import dynazzviewer.filesystem.hierarchy.FilePath
import dynazzviewer.filesystem.hierarchy.RootDirectory
import dynazzviewer.services.filesystem.VideoFile
import dynazzviewer.ui.viewmodels.DirectoryViewModel
import dynazzviewer.ui.viewmodels.NodeFactory
import dynazzviewer.ui.viewmodels.VideoFileViewModel
import java.io.File
import java.lang.RuntimeException
import javax.transaction.NotSupportedException
import org.junit.Assert
import org.junit.Test

class NodeFactoryTest {
    @Test
    fun subFolderTest() {
        val nodeFactory = NodeFactory(MockFileRepository())
        val root = DirectoryViewModel(
            name = "H:${File.separatorChar}downloads",
            fullPath = "H:${File.separatorChar}downloads",
            assignedParent = null
        )
        val map = mutableMapOf<String, DirectoryViewModel>()
        val dir = nodeFactory.getOrCreateParent(
            root,
            "H:${File.separatorChar}downloads${File.separatorChar}" +
                    "subfolder${File.separatorChar}file1.mkv",
            map
        )
        Assert.assertEquals(false, dir.isRoot)
        Assert.assertEquals("subfolder", dir.name)
        Assert.assertEquals(true, dir.children.isEmpty())
        Assert.assertEquals(1, map.count())
        Assert.assertEquals(
            "H:${File.separatorChar}downloads${File.separatorChar}subfolder",
            map.keys.single()
        )
        Assert.assertEquals(dir, map.values.single())
    }

    @Test
    fun nodeFactoryTest() {
        val nodeFactory = NodeFactory(MockFileRepository())
        val directory = nodeFactory.createRootViewModel(
            "H:${File.separatorChar}downloads"
        )
        Assert.assertEquals(2, directory.children.count())
        val secondFile = directory.children.singleOrNull { it.name == "File2.mkv" }
        Assert.assertNotNull(secondFile!!)
        Assert.assertTrue(secondFile is VideoFileViewModel)
        Assert.assertTrue(secondFile.children.isEmpty())
        val subFolder = directory.children.singleOrNull { it.name == "subfolder" }
        Assert.assertNotNull(subFolder!!)
        Assert.assertEquals("subfolder", subFolder.name)
        Assert.assertTrue(subFolder is DirectoryViewModel)
        Assert.assertEquals(1, subFolder.children.count())
        val firstFile = subFolder.children.singleOrNull()
        Assert.assertNotNull(firstFile!!)
        Assert.assertEquals("File1.mkv", firstFile.name)
        Assert.assertTrue(firstFile is VideoFileViewModel)
        Assert.assertTrue(firstFile.children.isEmpty())
    }

    class MockFileRepository : FileRepository {
        override fun updateFileName(fileName: FileName): Map<RootDirectory, Set<VideoFile>> {
            throw NotSupportedException()
        }

        override fun remove(rootPath: String): Boolean = throw RuntimeException("Not implemented")

        override fun add(rootPath: String): List<VideoFile> {
            val root = RootDirectory("H:${File.separatorChar}downloads")
            return listOf(
                VideoFile(
                    name = FileName("File2.mkv"),
                    path = FilePath(
                        "H:${File.separatorChar}downloads${File.separatorChar}File2.mkv"
                    ),
                    root = root,
                    mediaFileId = null,
                    viewStatus = ViewStatus.Viewed
                ),
                VideoFile(
                    name = FileName("File1.mkv"),
                    path = FilePath(
                        "H:${File.separatorChar}downloads${File.separatorChar}" +
                                "subfolder${File.separatorChar}File1.mkv"
                    ),
                    root = root,
                    mediaFileId = null,
                    viewStatus = ViewStatus.Viewed
                )
            )
        }
    }
}

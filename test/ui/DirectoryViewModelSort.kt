package ui

import dynazzviewer.base.ViewStatus
import dynazzviewer.ui.viewmodels.DirectoryViewModel
import dynazzviewer.ui.viewmodels.VideoFileViewModel
import org.junit.Assert
import org.junit.Test

class DirectoryViewModelSort {
    @Test
    fun sortDirectoryBeforeFileTest() {
        val directory = DirectoryViewModel(
            assignedParent = null,
            name = "Parent",
            fullPath = "."
        )
        directory.children.add(VideoFileViewModel(
            name = "A",
            parent = directory,
            viewStatus = ViewStatus.None,
            fullPath = "."
        ))
        directory.children.add(DirectoryViewModel(
            name = "A",
            assignedParent = directory,
            fullPath = "."
        ))
        Assert.assertTrue(directory.children.first() is DirectoryViewModel)
    }

    @Test
    fun sortDirectoryFileName() {
        val directory = DirectoryViewModel(
            assignedParent = null,
            name = "Parent",
            fullPath = "."
        )
        directory.children.add(VideoFileViewModel(
            name = "B",
            parent = directory,
            viewStatus = ViewStatus.None,
            fullPath = "."
        ))
        directory.children.add(VideoFileViewModel(
            name = "A",
            parent = directory,
            viewStatus = ViewStatus.None,
            fullPath = "."
        ))
        Assert.assertEquals("A", directory.children.first().name)
    }
}

package dynazzviewer.ui.web

import dynazzviewer.ui.tornado.FileSystemController
import dynazzviewer.ui.tornado.viewmodels.RootNodeViewModel
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import java.io.File

class FileSystemGraph(
    private val fileSystemController: FileSystemController
) {
    @GraphQLQuery
    fun fileSystemRoots(): RootNodeViewModel {
        return fileSystemController.fileSystemRoot
    }

    @GraphQLMutation
    fun addRootDirectory(rootPath: String) {
        val file = File(rootPath)
        fileSystemController.addRootDirectories(listOf(file))
    }

    @GraphQLMutation
    fun removeRootDirectory(rootPath: String) {
        fileSystemController.removeRootDirectory(rootPath)
    }
}

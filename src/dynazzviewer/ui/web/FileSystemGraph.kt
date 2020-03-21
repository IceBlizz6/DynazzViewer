package dynazzviewer.ui.web

import dynazzviewer.filesystem.FileRepository
import dynazzviewer.services.filesystem.VideoFile
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import java.io.File

class FileSystemGraph(
    private val controller: FileSystemController
) {
    @GraphQLQuery
    fun fileSystemRoots(): Map<String, Set<VideoFile>> {
        return controller.list()
    }

    @GraphQLMutation
    fun addRootDirectory(rootPath: String) {
        controller.addRootDirectory(rootPath)
    }

    @GraphQLMutation
    fun removeRootDirectory(rootPath: String) {
        controller.removeRootDirectory(rootPath)
    }

    @GraphQLMutation
    fun refreshRootDirectory(rootPath: String) {
        controller.refreshDirectory(rootPath)
    }
}

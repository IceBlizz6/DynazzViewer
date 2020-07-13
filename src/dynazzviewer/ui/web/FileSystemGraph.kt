package dynazzviewer.ui.web

import dynazzviewer.base.ViewStatus
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery

class FileSystemGraph(
    private val controller: FileSystemController
) {
    @GraphQLQuery
    fun listVideoFiles(): Set<RootEntry> {
        val videoFileMap = controller.list()
        return videoFileMap.map { e -> RootEntry(e.component1(), e.component2()) }.toSet()
    }

    @GraphQLMutation
    fun setViewStatus(videoFilePaths: Set<String>, status: ViewStatus): Map<String, Int> {
        return controller.setViewStatus(videoFilePaths, status)
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

    @GraphQLMutation
    fun showExplorer(path: String) {
        controller.showExplorer(path)
    }

    @GraphQLMutation
    fun playVideo(path: String) {
        controller.playVideos(listOf(path))
    }
}

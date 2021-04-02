package dynazzviewer.ui.web

import dynazzviewer.base.ViewStatus
import dynazzviewer.filesystem.FileNameDetector
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

    @GraphQLQuery
    fun parseFileNames(fileNames: List<String>): List<DetectedFileResult> {
        val detector = FileNameDetector()
        return fileNames.map { it to detector.parse(it) }.mapNotNull { pair ->
            val result = pair.second
            if (result == null) {
                null
            } else {
                DetectedFileResult(pair.first, result.name, result.season, result.episode)
            }
        }
    }

    @GraphQLMutation
    fun linkVideoFile(mediaFileId: Int, mediaPartId: Int) {
        controller.linkVideoFile(
            mediaFileId = mediaFileId,
            mediaPartId = mediaPartId
        )
    }

    class DetectedFileResult(
        val fileName: String,
        val name: String,
        val season: Int?,
        val episode: Int?
    )
}

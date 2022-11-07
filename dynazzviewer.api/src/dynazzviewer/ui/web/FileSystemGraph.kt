package dynazzviewer.ui.web

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dynazzviewer.entities.ViewStatus
import dynazzviewer.files.FileNameDetector
import dynazzviewer.files.hierarchy.RootDirectory
import dynazzviewer.files.hierarchy.VideoFile
import javax.swing.JFileChooser

class FileSystemGraph(
    builder: SchemaBuilder,
    private val controller: FileSystemController
) {
    init {
        builder.apply {
            enum<ViewStatus>()
            query("listVideoFiles") {
                resolver<Set<RootEntry>> {
                    controller
                        .list()
                        .map { e -> RootEntry(e.component1(), e.component2()) }.toSet()
                }
            }
            mutation("setViewStatus") {
                resolver { videoFilePaths: List<String>, status: ViewStatus ->
                    controller.setViewStatus(videoFilePaths.toSet(), status)
                        .map { MediaFileIdentification(it.key, it.value) }
                }
            }
            mutation("addRootDirectoryInteractively") {
                resolver<Boolean> {
                    val fileChooser = JFileChooser()
                    fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                    val option = fileChooser.showOpenDialog(null)
                    if (option == JFileChooser.APPROVE_OPTION) {
                        val file = fileChooser.selectedFile
                        addRootDirectory(file.path)
                    }
                    true
                }
            }
            mutation("addRootDirectory") {
                resolver { rootPath: String ->
                    addRootDirectory(rootPath)
                    true
                }
            }
            mutation("removeRootDirectory") {
                resolver { rootPath: String ->
                    controller.removeRootDirectory(rootPath)
                    true
                }
            }
            mutation("refreshDirectory") {
                resolver { path: String ->
                    controller.refreshDirectory(path)
                        .map { RootEntry(it.key.rootPath, it.value) }
                        .toSet()
                }
            }
            mutation("showExplorer") {
                resolver { path: String ->
                    controller.showExplorer(path)
                    true
                }
            }
            mutation("playVideo") {
                resolver { path: String ->
                    controller.playVideo(path)
                    true
                }
            }
            query("parseFileNames") {
                resolver { fileNames: List<String> ->
                    val detector = FileNameDetector()
                    fileNames.map { it to detector.parse(it) }.mapNotNull { pair ->
                        val result = pair.second
                        if (result == null) {
                            null
                        } else {
                            DetectedFileResult(
                                pair.first,
                                result.name,
                                result.season,
                                result.episode
                            )
                        }
                    }
                }
            }
            mutation("linkVideoFile") {
                resolver { mediaFileId: Int, mediaPartId: Int ->
                    controller.linkVideoFile(
                        mediaFileId = mediaFileId,
                        mediaPartId = mediaPartId
                    )
                    true
                }
            }
            mutation("linkVideoFileWithName") {
                resolver { mediaFileName: String, mediaPartId: Int ->
                    controller.linkVideoFile(
                        mediaFileName = mediaFileName,
                        mediaPartId = mediaPartId
                    )
                    true
                }
            }
        }
    }

    class RefreshedRoot(
        val root: RootDirectory,
        val files: Set<VideoFile>
    )

    class VideoFileKey(
        val id: Int,
        val fileName: String
    )

    private fun addRootDirectory(rootPath: String) {
        controller.addRootDirectory(rootPath)
    }

    class DetectedFileResult(
        val fileName: String,
        val name: String,
        val season: Int?,
        val episode: Int?
    )

    class MediaFileIdentification(
        val fileName: String,
        val mediaFileId: Int
    )
}

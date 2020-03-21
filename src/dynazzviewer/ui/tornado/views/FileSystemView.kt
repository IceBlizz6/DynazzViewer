package dynazzviewer.ui.tornado.views

import dynazzviewer.base.ViewStatus
import dynazzviewer.ui.tornado.MainController
import dynazzviewer.ui.tornado.viewmodels.DirectoryViewModel
import dynazzviewer.ui.tornado.viewmodels.NodeViewModel
import dynazzviewer.ui.tornado.viewmodels.UnknownViewModelException
import dynazzviewer.ui.tornado.viewmodels.VideoFileViewModel
import javafx.geometry.Pos
import javafx.scene.control.SelectionMode
import javafx.scene.control.TreeItem
import javafx.scene.input.TransferMode
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import tornadofx.*

class FileSystemView : View() {
    private val mainController: MainController by inject()
    private val controller = mainController.fileSystemController

    override val root = treeview<NodeViewModel>(TreeItem(controller.fileSystemRoot)) {
        isShowRoot = false
        selectionModel.selectionMode = SelectionMode.MULTIPLE
        setOnDragOver {
            if (it.dragboard.hasFiles() && it.dragboard.files.all { e -> e.isDirectory }) {
                it.acceptTransferModes(TransferMode.LINK)
            } else {
                it.consume()
            }
        }
        setOnDragDropped {
            if (it.dragboard.hasFiles() && it.dragboard.files.all { e -> e.isDirectory }) {
                controller.addRootDirectories(it.dragboard.files)
            }
            it.isDropCompleted = true
            it.consume()
        }
        cellFormat {
            graphic = hbox {
                when (it) {
                    is VideoFileViewModel -> {
                        if (it.viewStatus == ViewStatus.Viewed) {
                            circle {
                                fill = Color.GREEN
                                radius = 5.0
                                alignment = Pos.CENTER_LEFT
                            }
                        } else if (it.viewStatus == ViewStatus.Skipped) {
                            circle {
                                fill = Color.ORANGERED
                                radius = 5.0
                                alignment = Pos.CENTER_LEFT
                            }
                        } else if (it.viewStatus == ViewStatus.None) {
                            rectangle {
                                fill = Color.LIGHTBLUE
                                width = 10.0
                                height = 10.0
                                alignment = Pos.CENTER_LEFT
                            }
                        }
                        label {
                            text = it.name
                            textAlignment = TextAlignment.CENTER
                        }
                        contextmenu {
                            item("Flag viewed").action {
                                controller.setVideoViewStatus(
                                    selectedVideoFiles(),
                                    ViewStatus.Viewed
                                )
                                refresh()
                            }
                            item("Undo flag").action {
                                controller.setVideoViewStatus(
                                    selectedVideoFiles(),
                                    ViewStatus.None
                                )
                                refresh()
                            }
                            item("Flag skipped").action {
                                controller.setVideoViewStatus(
                                    selectedVideoFiles(),
                                    ViewStatus.Skipped
                                )
                                refresh()
                            }
                            item("Open in explorer").action {
                                controller.showExplorer(selected())
                            }
                            if (controller.playVideoEnabled) {
                                item("Play video(s)") {
                                    action {
                                        controller.playVideos(selectedVideoFiles())
                                    }
                                }
                            }
                        }
                    }
                    is DirectoryViewModel -> {
                        label(it.name)
                        contextmenu {
                            item("Flag all as viewed").action {
                                controller.setVideoViewStatusRecursive(
                                    selected(),
                                    ViewStatus.Viewed
                                )
                                refresh()
                            }
                            item("Undo all flags").action {
                                controller.setVideoViewStatusRecursive(
                                    selected(),
                                    ViewStatus.None
                                )
                                refresh()
                            }
                            item("Flag all as skipped").action {
                                controller.setVideoViewStatusRecursive(
                                    selected(),
                                    ViewStatus.Skipped
                                )
                                refresh()
                            }
                            item("Open in explorer").action {
                                controller.showExplorer(selected())
                            }
                            item("Refresh").action {
                                controller.refreshDirectory(it)
                            }
                            if (it.isRoot) {
                                item("Remove") {
                                    action {
                                        selected()
                                            .filterIsInstance<DirectoryViewModel>()
                                            .filter { it.isRoot }
                                            .forEach { controller.removeRootDirectory(it.name) }
                                    }
                                }
                            }
                        }
                    }
                    else -> throw UnknownViewModelException("Unknown FS node type: $it")
                }
            }
        }
        populate {
            it.value.children
        }
    }

    private fun selected(): List<NodeViewModel> {
        return root.selectionModel.selectedItems.map { e -> e.value }
    }

    private fun selectedVideoFiles(): List<VideoFileViewModel> {
        return selected()
            .filterIsInstance(VideoFileViewModel::class.java)
    }
}

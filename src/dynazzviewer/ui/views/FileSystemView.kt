package dynazzviewer.ui.views

import dynazzviewer.base.ViewStatus
import dynazzviewer.ui.MainController
import dynazzviewer.ui.viewmodels.DirectoryViewModel
import dynazzviewer.ui.viewmodels.NodeViewModel
import dynazzviewer.ui.viewmodels.UnknownViewModelException
import dynazzviewer.ui.viewmodels.VideoFileViewModel
import javafx.geometry.Pos
import javafx.scene.control.SelectionMode
import javafx.scene.control.TreeItem
import javafx.scene.input.TransferMode
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import tornadofx.*

class FileSystemView : View() {
    private val mainViewModel: MainController by inject()

    override val root = treeview<NodeViewModel>(TreeItem(mainViewModel.fileSystemRoot)) {
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
                mainViewModel.addRootDirectories(it.dragboard.files)
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
                                radius = 7.0
                            }
                        } else if (it.viewStatus == ViewStatus.Skipped) {
                            circle {
                                fill = Color.ORANGE
                                radius = 7.0
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
                    }
                    is DirectoryViewModel -> {
                        label(it.name)
                        contextmenu {
                            if (it.isRoot) {
                                item("Remove") {
                                    action {
                                        selected()
                                            .filterIsInstance<DirectoryViewModel>()
                                            .filter { it.isRoot }
                                            .forEach { mainViewModel.removeRootDirectory(it.name) }
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
}

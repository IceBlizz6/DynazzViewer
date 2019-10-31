package dynazzviewer.ui.views

import dynazzviewer.ui.MainController
import dynazzviewer.ui.viewmodels.DirectoryViewModel
import dynazzviewer.ui.viewmodels.NodeViewModel
import dynazzviewer.ui.viewmodels.UnknownViewModelException
import dynazzviewer.ui.viewmodels.VideoFileViewModel
import javafx.scene.control.SelectionMode
import javafx.scene.control.TreeItem
import javafx.scene.image.Image
import tornadofx.*

class FileSystemView : View() {
    private val mainViewModel: MainController by inject()

    override val root = treeview<NodeViewModel>(TreeItem(mainViewModel.fileSystemRoot)) {
        isShowRoot = false
        selectionModel.selectionMode = SelectionMode.MULTIPLE

        cellFormat {
            graphic = hbox {
                when (it) {
                    is VideoFileViewModel -> label(it.name)
                    is DirectoryViewModel -> label(it.name)
                    else -> throw UnknownViewModelException("Unknown FS node type: $it")
                }
            }
        }
        populate {
            it.value.children
        }
    }
}

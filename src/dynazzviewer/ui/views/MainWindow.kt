package dynazzviewer.ui.views

import javafx.scene.control.TabPane
import tornadofx.*

class MainWindow : View("DynazzViewer") {
    override val root = borderpane {
        minWidth = 800.0
        minHeight = 600.0
        center {
            tabpane {
                tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                tab(text = "Files") {
                    this += find<FileSystemView>()
                }
            }
        }
    }
}

package dynazzviewer.ui.views

import tornadofx.View
import tornadofx.borderpane

class MainWindow : View("DynazzViewer") {
    override val root = borderpane {
        minWidth = 800.0
        minHeight = 600.0
    }
}

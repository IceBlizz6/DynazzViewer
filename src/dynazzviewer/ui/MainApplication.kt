package dynazzviewer.ui

import dynazzviewer.ui.views.MainWindow
import tornadofx.App

class MainApplication : App(MainWindow::class) {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(MainApplication::class.java, *args)
        }
    }
}

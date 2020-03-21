package dynazzviewer.ui.tornado

import dynazzviewer.ui.tornado.views.MainWindow
import tornadofx.App

class DesktopApplication : App(MainWindow::class) {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(DesktopApplication::class.java, *args)
        }
    }
}

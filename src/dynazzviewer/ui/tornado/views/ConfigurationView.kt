package dynazzviewer.ui.tornado.views

import dynazzviewer.ui.tornado.MainController
import dynazzviewer.ui.tornado.inputmodels.ConfigurationModel
import tornadofx.*

class ConfigurationView : View("Configuration") {
    private val mainController: MainController by inject()
    private val configurationModel: ConfigurationModel

    init {
        configurationModel =
            ConfigurationModel(mainController.configuration)
    }

    override val root = vbox {
        maxWidth = 800.0
        maxHeight = 600.0
        form {
            fieldset {
                field("Media player application path") {
                    textfield(configurationModel.mediaPlayerApplicationPath)
                }
            }
        }
        button {
            text = "Save"
            action {
                configurationModel.save()
                close()
            }
        }
    }
}

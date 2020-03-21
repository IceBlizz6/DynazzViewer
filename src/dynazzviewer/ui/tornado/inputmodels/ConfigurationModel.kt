package dynazzviewer.ui.tornado.inputmodels

import dynazzviewer.ui.config.DefaultConfiguration
import javafx.beans.property.SimpleStringProperty

class ConfigurationModel(
    val configuration: DefaultConfiguration
) {
    val mediaPlayerApplicationPath =
        SimpleStringProperty(configuration.mediaPlayerApplicationPath)

    fun save() {
        configuration.mediaPlayerApplicationPath =
            mediaPlayerApplicationPath.value
    }
}

package dynazzviewer.ui.tornado.inputmodels

import dynazzviewer.base.ExtDatabase
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.StringProperty
import tornadofx.stringProperty

class MediaSearchModel {
    val name: StringProperty = stringProperty("")
    val selectedExtDatabase = SimpleObjectProperty<ExtDatabase>(ExtDatabase.MyAnimeList)
}

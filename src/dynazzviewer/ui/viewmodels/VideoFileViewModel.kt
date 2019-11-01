package dynazzviewer.ui.viewmodels

import dynazzviewer.base.ViewStatus
import javafx.beans.property.SimpleObjectProperty
import tornadofx.getValue
import tornadofx.setValue

class VideoFileViewModel(
    name: String,
    viewStatus: ViewStatus
) : NodeViewModel(name) {
    private val viewStatusProperty: SimpleObjectProperty<ViewStatus> =
        SimpleObjectProperty(viewStatus)

    var viewStatus: ViewStatus by viewStatusProperty
}

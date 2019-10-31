package dynazzviewer.ui.viewmodels

import javafx.beans.property.SimpleBooleanProperty
import tornadofx.getValue
import tornadofx.setValue

class VideoFileViewModel(
    name: String,
    isViewed: Boolean
) : NodeViewModel(name) {
    private val isViewedProperty: SimpleBooleanProperty = SimpleBooleanProperty(isViewed)

    var isViewed by isViewedProperty
}

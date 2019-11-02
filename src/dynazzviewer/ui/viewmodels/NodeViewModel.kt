package dynazzviewer.ui.viewmodels

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.getValue
import tornadofx.setValue

abstract class NodeViewModel(
    name: String,
    val fullPath: String
) {
    private val childrenProperty = SimpleListProperty<NodeViewModel>(
        FXCollections.observableArrayList(listOf()))
    private val nameProperty: SimpleStringProperty = SimpleStringProperty(name)

    val children: ObservableList<NodeViewModel> by childrenProperty
    var name: String by nameProperty
}

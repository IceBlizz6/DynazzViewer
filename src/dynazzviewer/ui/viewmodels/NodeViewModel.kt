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

    abstract val parent: NodeViewModel

    fun videoFiles(): List<VideoFileViewModel> {
        val list = mutableListOf<VideoFileViewModel>()
        list.addAll(
            children.filterIsInstance<VideoFileViewModel>()
        )
        list.addAll(
            children
                .filterIsInstance<DirectoryViewModel>()
                .flatMap { it.videoFiles() }
        )
        return list
    }

    fun directories(): List<DirectoryViewModel> {
        val list = children
            .filterIsInstance<DirectoryViewModel>()
            .toMutableList()
        list.addAll(
            list.flatMap { it.directories() }
        )
        return list
    }
}

package dynazzviewer.ui.tornado.viewmodels

import java.nio.file.Paths
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.SortedFilteredList
import tornadofx.getValue
import tornadofx.setValue

abstract class NodeViewModel(
    name: String,
    val fullPath: String
) {
    private val observableChildrenList = FXCollections.observableArrayList<NodeViewModel>(listOf())
    private val sortedChildrenList = SortedFilteredList<NodeViewModel>(observableChildrenList)
    private val sortedChildrenProperty = SimpleListProperty<NodeViewModel>(sortedChildrenList)
    private val nameProperty: SimpleStringProperty = SimpleStringProperty(name)

    val children: ObservableList<NodeViewModel> by sortedChildrenProperty
    var name: String by nameProperty

    abstract val parent: NodeViewModel

    init {
        sortedChildrenList.sortedItems.comparator = NodeComparator()
    }

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

    fun lookup(path: String): NodeViewModel {
        val pathSteps = Paths.get(path)
        var current = this
        for (pathStep in pathSteps) {
            current = current.children.single { it.name == pathStep.toString() }
        }
        return current
    }
}

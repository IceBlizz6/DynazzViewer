package dynazzviewer.ui.viewmodels

class RootNodeViewModel() : NodeViewModel("Root", "Root") {
    val childrenAsDirectories: List<DirectoryViewModel>
        get() = children.filterIsInstance<DirectoryViewModel>()

    override val parent: NodeViewModel
        get() = throw NotImplementedError()
}

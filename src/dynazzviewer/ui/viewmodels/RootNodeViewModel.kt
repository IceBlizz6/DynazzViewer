package dynazzviewer.ui.viewmodels

class RootNodeViewModel() : NodeViewModel("Root", "Root") {
    override val parent: NodeViewModel
        get() = throw NotImplementedError()
}

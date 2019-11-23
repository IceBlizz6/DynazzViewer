package dynazzviewer.ui.viewmodels

class DirectoryViewModel(
    name: String,
    fullPath: String,
    private val assignedParent: NodeViewModel?
) : NodeViewModel(name, fullPath) {
    val isRoot: Boolean
        get() {
            return assignedParent == null
        }

    override val parent: NodeViewModel
        get() {
            if (assignedParent == null) {
                throw NotImplementedError()
            } else {
                return assignedParent
            }
        }
}

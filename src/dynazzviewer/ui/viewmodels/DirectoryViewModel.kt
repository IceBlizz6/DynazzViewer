package dynazzviewer.ui.viewmodels

class DirectoryViewModel(
    name: String,
    fullPath: String,
    val isRoot: Boolean
) : NodeViewModel(name, fullPath)

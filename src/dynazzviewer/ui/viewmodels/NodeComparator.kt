package dynazzviewer.ui.viewmodels

class NodeComparator : Comparator<NodeViewModel> {
    override fun compare(first: NodeViewModel?, second: NodeViewModel?): Int {
        if (first is DirectoryViewModel) {
            if (second is DirectoryViewModel) {
                return first.name.compareTo(second.name)
            } else if (second is VideoFileViewModel) {
                return -1
            } else {
                return 0
            }
        } else if (first is VideoFileViewModel) {
            if (second is DirectoryViewModel) {
                return 1
            } else if (second is VideoFileViewModel) {
                return first.name.compareTo(second.name)
            } else {
                return 0
            }
        } else {
            return 0
        }
    }
}

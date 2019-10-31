package dynazzviewer.ui.viewmodels

import dynazzviewer.base.ViewStatus
import dynazzviewer.filesystem.FileRepository
import java.io.File

class NodeFactory(
    private val fileRepository: FileRepository
) {
    fun createRootViewModel(path: String, isRoot: Boolean):
            DirectoryViewModel {
        val rootViewModel = DirectoryViewModel(path, isRoot)
        val videoFiles = fileRepository.add(path)
        val map = mutableMapOf<String, DirectoryViewModel>()
        for (videoFile in videoFiles) {
            val parent = getOrCreateParent(rootViewModel, videoFile.path.path, map)
            parent.children.add(VideoFileViewModel(
                    videoFile.name.name,
                    videoFile.viewStatus == ViewStatus.Viewed)
                )
        }
        return rootViewModel
    }

    fun getOrCreateParent(
        rootViewModel: DirectoryViewModel,
        path: String,
        map: MutableMap<String, DirectoryViewModel>
    ): DirectoryViewModel {
        val parentPath = File(path).parent
        if (rootViewModel.name == parentPath) {
            return rootViewModel
        } else {
            val directory: DirectoryViewModel? = map[parentPath]
            if (directory == null) {
                val parent = getOrCreateParent(rootViewModel, parentPath, map)
                val parentName = File(parentPath).name
                val current = DirectoryViewModel(parentName, false)
                map[parentPath] = current
                parent.children.add(current)
                return current
            } else {
                return directory
            }
        }
    }
}

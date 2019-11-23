package dynazzviewer.ui.viewmodels

import dynazzviewer.filesystem.FileRepository
import dynazzviewer.services.filesystem.VideoFile
import java.io.File

class NodeFactory(
    private val fileRepository: FileRepository
) {
    fun createRootViewModel(path: String):
            DirectoryViewModel {
        val rootViewModel = DirectoryViewModel(
            name = path,
            fullPath = path,
            assignedParent = null
        )
        val videoFiles = fileRepository.add(path)
        val map = mutableMapOf<String, DirectoryViewModel>()
        for (videoFile in videoFiles) {
            val parent = getOrCreateParent(rootViewModel, videoFile.path.path, map)
            parent.children.add(
                createVideoFileViewModel(parent, videoFile)
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
                val current = DirectoryViewModel(
                    name = parentName,
                    fullPath = parentPath,
                    assignedParent = parent
                )
                map[parentPath] = current
                parent.children.add(current)
                return current
            } else {
                return directory
            }
        }
    }

    private fun createVideoFileViewModel(
        parent: DirectoryViewModel,
        videoFile: VideoFile
    ): VideoFileViewModel {
        return VideoFileViewModel(
            name = videoFile.name.name,
            fullPath = videoFile.path.path,
            parent = parent,
            viewStatus = videoFile.viewStatus
        )
    }
}

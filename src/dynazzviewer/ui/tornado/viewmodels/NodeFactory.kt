package dynazzviewer.ui.tornado.viewmodels

import dynazzviewer.filesystem.FileRepository
import dynazzviewer.filesystem.hierarchy.FilePath
import dynazzviewer.filesystem.hierarchy.RootDirectory
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

    fun refreshDirectory(fileSystemRoot: RootNodeViewModel, directoryPath: FilePath) {
        val updatedEntries = fileRepository.refreshDirectory(directoryPath.path)
        val matches = findMatching(fileSystemRoot, directoryPath.path)
        for (match in matches) {
            match.value.children.clear()
        }
        fillDirectory(fileSystemRoot, updatedEntries)
    }

    private fun findMatching(
        fileSystemRoot: RootNodeViewModel,
        directoryPath: String
    ): Map<DirectoryViewModel, DirectoryViewModel> {
        val map = mutableMapOf<DirectoryViewModel, DirectoryViewModel>()
        for (root in fileSystemRoot.childrenAsDirectories) {
            if (root.fullPath == directoryPath) {
                map[root] = root
            } else if (root.fullPath.startsWith(directoryPath)) {
                map[root] = root.lookup(directoryPath) as DirectoryViewModel
            } else if (directoryPath.startsWith(root.fullPath)) {
                map[root] = root
            }
        }
        return map
    }

    private fun fillDirectory(
        fileSystemRoot: RootNodeViewModel,
        updatedEntries: Map<RootDirectory, Set<VideoFile>>
    ) {
        for (entry in updatedEntries) {
            val rootViewModel = fileSystemRoot
                .childrenAsDirectories
                .single { it.name == entry.key.rootPath }
            refreshDirectoryNode(rootViewModel, entry.value)
        }
    }

    private fun refreshDirectoryNode(
        rootViewModel: DirectoryViewModel,
        videoFiles: Set<VideoFile>
    ) {
        val cacheMap = mutableMapOf<String, DirectoryViewModel>()
        cacheMap[rootViewModel.name] = rootViewModel
        cacheMap.putAll(
            rootViewModel.directories().map { it.fullPath to it }
        )
        for (videoFile in videoFiles) {
            refreshDirectoryVideoFile(rootViewModel, videoFile, cacheMap)
        }
    }

    private fun refreshDirectoryVideoFile(
        rootViewModel: DirectoryViewModel,
        videoFile: VideoFile,
        cacheMap: MutableMap<String, DirectoryViewModel>
    ) {
        val parent = getOrCreateParent(
            rootViewModel = rootViewModel,
            path = videoFile.path.path,
            map = cacheMap
        )
        val child: NodeViewModel? = parent
            .children
            .singleOrNull { it.fullPath == videoFile.path.path }
        if (child != null) {
            parent.children.remove(child)
        }
        val viewModel = createVideoFileViewModel(parent, videoFile)
        parent.children.add(viewModel)
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

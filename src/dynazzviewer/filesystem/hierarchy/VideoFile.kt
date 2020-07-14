package dynazzviewer.services.filesystem

import dynazzviewer.base.ViewStatus
import dynazzviewer.filesystem.hierarchy.FileName
import dynazzviewer.filesystem.hierarchy.FilePath
import dynazzviewer.filesystem.hierarchy.RootDirectory

class VideoFile(
    fileName: FileName,
    filePath: FilePath,
    root: RootDirectory,
    val mediaFileId: Int?,
    val viewStatus: ViewStatus
) : FileEntry(root, filePath, fileName)

package dynazzviewer.files.hierarchy

import dynazzviewer.entities.ViewStatus

class VideoFile(
    fileName: FileName,
    filePath: FilePath,
    root: RootDirectory,
    val mediaFileId: Int?,
    val viewStatus: ViewStatus,
    val linkedMediaPartId: Int?
) : FileEntry(root, filePath, fileName)

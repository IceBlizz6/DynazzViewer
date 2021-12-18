package dynazzviewer.services.filesystem

import dynazzviewer.filesystem.hierarchy.FileName
import dynazzviewer.filesystem.hierarchy.FilePath
import dynazzviewer.filesystem.hierarchy.RootDirectory

abstract class FileEntry(
    val root: RootDirectory,
    val filePath: FilePath,
    val fileName: FileName
)

package dynazzviewer.ui.web

import dynazzviewer.services.filesystem.VideoFile

class RootEntry(
    val root: String,
    val files: Set<VideoFile>
)

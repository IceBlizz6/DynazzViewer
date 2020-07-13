package dynazzviewer.ui.web

import dynazzviewer.entities.plugins.InferGraphQlNullity
import dynazzviewer.services.filesystem.VideoFile

@InferGraphQlNullity
class RootEntry(
    val root: String,
    val files: Set<VideoFile>
)

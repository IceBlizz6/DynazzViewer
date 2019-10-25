package base

import dynazzviewer.controllers.UpdateListener

class TestUpdateListener : UpdateListener {
    var mediaPartCounter = 0
    var mediaFileCounter = 0

    override fun updateMediaUnit(id: Int, recursive: Boolean) {
    }

    override fun setMediaFileId(name: String, assignedId: Int) {
    }

    override fun updateMediaPart(id: Int) {
        mediaPartCounter++
    }

    override fun updateMediaFile(id: Int) {
        mediaFileCounter++
    }
}

package dynazzviewer.controllers

import dynazzviewer.base.ViewStatus
import dynazzviewer.storage.Storage
import java.util.*

class ViewStatusController(
	private val storage : Storage,
	private val listener : UpdateListener
) {
	fun setMediaPartStatus(status : ViewStatus, id : Int) {
		var mediaFileId : Int? = null
		storage.readWrite().use { context ->
			val mediaPart = context.mediaPartById(id);
			mediaPart.status = status
			Optional.ofNullable(mediaPart.mediaFile).ifPresent {e -> e.status = status; mediaFileId=e.id}
			context.save(mediaPart)
		}
		listener.updateMediaPart(id)
		Optional.ofNullable(mediaFileId).ifPresent {e -> listener.updateMediaFile(e)}
	}
	
	fun setMediaFileStatus(status : ViewStatus, id : Int) {
		var mediaPartId : Int? = null
		storage.readWrite().use { context ->
			val mediaFile = context.mediaFileById(id);
			mediaFile.status = status
			Optional.ofNullable(mediaFile.mediaPart).ifPresent {e -> e.status = status; mediaPartId = e.id}
			context.save(mediaFile)
		}
		listener.updateMediaFile(id)
		Optional.ofNullable(mediaPartId).ifPresent { e -> listener.updateMediaPart(e) }
	}
	
	fun link(mediaPartId : Int, mediaFileId : Int) {
		storage.readWrite().use { context ->
			val mediaPart = context.mediaPartById(mediaPartId)
			val mediaFile = context.mediaFileById(mediaFileId)
			val sharedStatus = resolveViewStatus(mediaPart.status, mediaFile.status)
			mediaPart.status = sharedStatus
			mediaFile.status = sharedStatus
			mediaFile.mediaPart = mediaPart
			context.save(mediaPart)
			context.save(mediaFile)
		}
		listener.updateMediaFile(mediaFileId)
		listener.updateMediaPart(mediaPartId)
	}
	
	fun resolveViewStatus(vararg statusList: ViewStatus) : ViewStatus {
		return statusList.max()!!
	}
}

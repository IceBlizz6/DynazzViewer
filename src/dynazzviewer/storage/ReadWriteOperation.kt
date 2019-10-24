package dynazzviewer.storage

import dynazzviewer.entities.EntityModel
import dynazzviewer.entities.MediaUnitTag

interface ReadWriteOperation : ReadOperation {
	fun tagsGetOrCreate(tagNames: List<String>) : List<MediaUnitTag>
	
	fun save(entity : EntityModel)
}

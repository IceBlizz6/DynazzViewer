package dynazzviewer.storage

import dynazzviewer.entities.EntityModel
import dynazzviewer.entities.MediaUnitTag

interface ReadWriteOperation : ReadOperation {
    fun tagsGetOrCreate(tagNames: Set<String>): List<MediaUnitTag>

    fun save(entity: EntityModel)

    fun delete(entity: EntityModel)
}

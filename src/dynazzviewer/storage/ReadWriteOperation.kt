package dynazzviewer.storage

import dynazzviewer.base.ExtDatabase
import dynazzviewer.entities.EntityModel
import dynazzviewer.entities.MediaDatabaseEntry
import dynazzviewer.entities.MediaUnitTag

interface ReadWriteOperation : ReadOperation {
    fun tagsGetOrCreate(tagNames: Set<String>): List<MediaUnitTag>

    fun mediaEntryGetOrCreate(extDb: ExtDatabase, extDbCode: String): MediaDatabaseEntry

    fun save(entity: EntityModel)

    fun delete(entity: EntityModel)
}

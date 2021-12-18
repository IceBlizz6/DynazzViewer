package dynazzviewer.storage

import dynazzviewer.entities.AnimeSeasonFlagState
import dynazzviewer.entities.EntityModel
import dynazzviewer.entities.ExtDatabase
import dynazzviewer.entities.MediaDatabaseEntry
import dynazzviewer.entities.MediaUnitTag

interface ReadWriteOperation : ReadOperation {
    fun tagsGetOrCreate(tagNames: Set<String>): List<MediaUnitTag>

    fun mediaEntryGetOrCreate(extDb: ExtDatabase, extDbCode: String): MediaDatabaseEntry

    fun save(entity: EntityModel)

    fun delete(entity: EntityModel)

    fun setAnimeSeasonFlag(malId: Int, flag: AnimeSeasonFlagState)
}

package dynazzviewer.services.descriptors

import dynazzviewer.base.ExtDatabase
import dynazzviewer.base.UniqueKey
import dynazzviewer.entities.MediaPart
import dynazzviewer.entities.MediaPartCollection
import dynazzviewer.storage.ReadWriteOperation
import java.time.LocalDate

class DescriptionPart(
    private val index: Int,
    val aired: LocalDate?,
    private val name: String,
    override val uniqueKey: String,
    val episodeNumber: Int?,
    val extDatabase: ExtDatabase,
    val extCode: String
) : UniqueKey {
    fun create(parent: MediaPartCollection, context: ReadWriteOperation): MediaPart {
        return MediaPart(
            parent = parent,
            sortOrder = index,
            name = name,
            uniqueExtKey = uniqueKey,
            aired = aired,
            episodeNumber = episodeNumber,
            databaseEntry = context.mediaEntryGetOrCreate(extDbCode = extCode, extDb = extDatabase)
        )
    }

    fun update(target: MediaPart) {
        target.name = name
        target.sortOrder = index
        target.aired = aired
        target.episodeNumber = episodeNumber
    }
}

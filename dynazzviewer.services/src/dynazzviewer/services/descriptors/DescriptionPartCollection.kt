package dynazzviewer.services.descriptors

import dynazzviewer.entities.AlternativeTitle
import dynazzviewer.entities.ExtDatabase
import dynazzviewer.entities.MediaPartCollection
import dynazzviewer.entities.MediaUnit
import dynazzviewer.entities.UniqueKey
import dynazzviewer.services.KeyMatcher
import dynazzviewer.storage.ReadWriteOperation

class DescriptionPartCollection(
    val name: String,
    val episodes: List<DescriptionPart>,
    val extDatabase: ExtDatabase?,
    val extDatabaseCode: String?,
    override val uniqueKey: String,
    val sortOrder: Int?,
    val seasonNumber: Int?,
    val alternativeTitles: List<String>?
) : UniqueKey {
    fun create(parent: MediaUnit, context: ReadWriteOperation): MediaPartCollection {
        val partCollection = MediaPartCollection(
            parent = parent,
            uniqueExtKey = uniqueKey,
            name = name,
            sortOrder = sortOrder,
            seasonNumber = seasonNumber,
            databaseEntry = if (extDatabase != null && extDatabaseCode != null) {
                context.mediaEntryGetOrCreate(extDb = extDatabase, extDbCode = extDatabaseCode)
            } else { null }
        )
        context.save(partCollection)
        if (alternativeTitles != null) {
            alternativeTitles
                .map { AlternativeTitle(partCollection, it) }
                .forEach { context.save(it) }
        }
        return partCollection
    }

    fun update(target: MediaPartCollection, context: ReadWriteOperation) {
        target.name = name
        target.sortOrder = target.sortOrder
        val matchResult = KeyMatcher().match(target.children, episodes)
        target.seasonNumber = seasonNumber
        if (alternativeTitles == null) {
            for (altTitle in target.alternativeTitles) {
                context.delete(altTitle)
            }
        } else {
            updateAlternativeTitles(context, target, alternativeTitles)
        }
        for (added in matchResult.added) {
            val mediaPart = added.create(target, context)
            context.save(mediaPart)
        }
        for (removed in matchResult.removed) {
            context.delete(removed)
        }
        for (matched in matchResult.matched) {
            matched.value.update(matched.key)
        }
    }

    private fun updateAlternativeTitles(
        context: ReadWriteOperation,
        parent: MediaPartCollection,
        titles: List<String>
    ) {
        val matchResult = KeyMatcher().matchWithString(parent.alternativeTitles, titles)
        for (added in matchResult.added) {
            val altTitle = AlternativeTitle(
                parent = parent,
                name = added
            )
            context.save(altTitle)
        }
        for (removed in matchResult.removed) {
            context.delete(removed)
        }
    }
}

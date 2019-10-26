package dynazzviewer.services.descriptors

import dynazzviewer.base.ExtDatabase
import dynazzviewer.base.Matcher
import dynazzviewer.base.UniqueKey
import dynazzviewer.entities.AlternativeTitle
import dynazzviewer.entities.MediaPartCollection
import dynazzviewer.entities.MediaUnit
import dynazzviewer.storage.ReadWriteOperation

class DescriptionPartCollection(
    val name: String,
    val episodes: List<DescriptionPart>,
    val extDatabaseCode: String?,
    val extDatabase: ExtDatabase?,
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
            seasonNumber = seasonNumber
        )
        if (alternativeTitles != null) {
            updateAlternativeTitles(context, partCollection, alternativeTitles)
        }
        return partCollection
    }

    fun update(target: MediaPartCollection, context: ReadWriteOperation) {
        target.name = name
        target.sortOrder = target.sortOrder
        val matchResult = Matcher().match(target.children, episodes)
        target.seasonNumber = seasonNumber
        if (alternativeTitles == null) {
            for (altTitle in target.alternativeTitles) {
                context.delete(altTitle)
            }
        } else {
            updateAlternativeTitles(context, target, alternativeTitles)
        }
        for (added in matchResult.added) {
            val mediaPart = added.create(target)
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
        val matchResult = Matcher().matchWithString(parent.alternativeTitles, titles)
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

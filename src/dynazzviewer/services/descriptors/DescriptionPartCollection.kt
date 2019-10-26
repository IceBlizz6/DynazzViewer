package dynazzviewer.services.descriptors

import dynazzviewer.base.ExtDatabase
import dynazzviewer.base.Matcher
import dynazzviewer.base.UniqueKey
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
    val alternativeTitles : List<String>?
) : UniqueKey {
    fun create(parent: MediaUnit): MediaPartCollection {
        val partCollection = MediaPartCollection(
            parent = parent,
            uniqueExtKey = uniqueKey,
            name = name,
            sortOrder = sortOrder,
            seasonNumber = seasonNumber
        )
        partCollection.alternativeTitles = alternativeTitles
        return partCollection
    }

    fun update(target: MediaPartCollection, context: ReadWriteOperation) {
        target.name = name
        target.sortOrder = target.sortOrder
        val matchResult = Matcher().match(target.children, episodes)
        target.seasonNumber = seasonNumber
        target.alternativeTitles = alternativeTitles
        for (added in matchResult.added) {
            val mediaPart = added.create(target)
            context.save(mediaPart)
            target.children.add(mediaPart)
        }
        for (removed in matchResult.removed) {
            target.children.remove(removed)
        }
        for (matched in matchResult.matched) {
            matched.value.update(matched.key)
        }
    }
}

package dynazzviewer.services.descriptors

import dynazzviewer.entities.ExtDatabase
import dynazzviewer.entities.MediaImage
import dynazzviewer.entities.MediaUnit
import dynazzviewer.services.KeyMatcher
import dynazzviewer.storage.ReadWriteOperation

class DescriptionUnit(
    val name: String,
    val children: List<DescriptionPartCollection>,
    val imageUrls: Set<String>,
    val tags: Set<String>,
    val uniqueKey: String?,
    val extDatabase: ExtDatabase?,
    val extDatabaseCode: String?
) {
    fun create(context: ReadWriteOperation): MediaUnit {
        val mediaUnit = MediaUnit(
            uniqueKey = uniqueKey,
            name = name,
            databaseEntry = extDatabase?.let { context.mediaEntryGetOrCreate(
                extDatabase,
                extDatabaseCode!!
            ) }
        )
        context.save(mediaUnit)
        imageUrls
            .map { e -> MediaImage(mediaUnit = mediaUnit, url = e) }
            .forEach { context.save(it) }
        mediaUnit.tags.addAll(
            context.tagsGetOrCreate(tags)
        )
        return mediaUnit
    }

    fun update(target: MediaUnit, context: ReadWriteOperation) {
        target.name = name
        updateImages(target, context)
        updateTags(target, context)
        updateChildren(target, context)
    }

    private fun updateImages(target: MediaUnit, context: ReadWriteOperation) {
        val matchResult = KeyMatcher().matchWithString(target.images, imageUrls)
        matchResult.added
            .map { e -> MediaImage(mediaUnit = target, url = e) }
            .forEach { context.save(it) }
        matchResult.removed.forEach { context.delete(it) }
    }

    private fun updateTags(target: MediaUnit, context: ReadWriteOperation) {
        val matchResult = KeyMatcher().matchWithString(target.tags, tags)
        val addedTags = context.tagsGetOrCreate(matchResult.added.toSet())
        target.tags.addAll(addedTags)
        target.tags.removeAll(matchResult.removed)
    }

    private fun updateChildren(target: MediaUnit, context: ReadWriteOperation) {
        val matchResult = KeyMatcher().match(target.children, children)
        for (added in matchResult.added) {
            added.create(target, context)
        }
        for (removed in matchResult.removed) {
            context.delete(removed)
        }
        for (matched in matchResult.matched) {
            matched.value.update(matched.key, context)
        }
    }
}

package dynazzviewer.services.descriptors

import dynazzviewer.base.Matcher
import dynazzviewer.entities.MediaImage
import dynazzviewer.entities.MediaUnit
import dynazzviewer.storage.ReadWriteOperation

class DescriptionUnit(
    val name: String,
    val children: List<DescriptionPartCollection>,
    val imageUrls: List<String>,
    val tags: List<String>,
    val uniqueKey: String?
) {
    fun create(context: ReadWriteOperation): MediaUnit {
        val mediaUnit = MediaUnit(
            uniqueExtKey = uniqueKey,
            name = name
        )
        mediaUnit.images.addAll(
            imageUrls.map { e -> MediaImage(mediaUnit = mediaUnit, url = e) }
        )
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
        val matchResult = Matcher().matchWithString(target.images, imageUrls)
        val addedImages = matchResult.added.map { e ->
            val image = MediaImage(mediaUnit = target, url = e)
            context.save(image)
            image
        }
        target.images.addAll(addedImages)
        target.images.removeAll(matchResult.removed)
    }

    private fun updateTags(target: MediaUnit, context: ReadWriteOperation) {
        val matchResult = Matcher().matchWithString(target.tags, tags)
        val addedTags = context.tagsGetOrCreate(matchResult.added)
        target.tags.addAll(addedTags)
        target.tags.removeAll(matchResult.removed)
    }

    private fun updateChildren(target: MediaUnit, context: ReadWriteOperation) {
        val matchResult = Matcher().match(target.children, children)
        for (added in matchResult.added) {
            val child = added.create(target, context)
            context.save(child)
        }
        for (removed in matchResult.removed) {
            context.delete(removed)
        }
        for (matched in matchResult.matched) {
            matched.value.update(matched.key, context)
        }
    }
}

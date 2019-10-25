package dynazzviewer.services.descriptors

import dynazzviewer.base.UniqueKey
import dynazzviewer.entities.MediaPart
import dynazzviewer.entities.MediaPartCollection
import java.time.LocalDate

class DescriptionPart(
    private val index: Int,
    val aired: LocalDate?,
    private val name: String,
    override val uniqueKey: String
) : UniqueKey {
    fun create(parent: MediaPartCollection): MediaPart {
        return MediaPart(
            parent = parent,
            sortOrder = index,
            name = name,
            uniqueExtKey = uniqueKey,
            aired = aired
        )
    }

    fun update(target: MediaPart) {
        target.name = name
        target.sortOrder = index
        target.aired = aired
    }
}

package dynazzviewer.services.descriptors.jikan

import com.fasterxml.jackson.annotation.JsonValue

enum class RelatedType(
    @JsonValue
       private val value: String,
    val includeInBatch: Boolean
) {
    ADAPTATION("Adaptation", false),
    SIDE_STORY("Side story", true),
    PARENT_STORY("Parent story", true),
}

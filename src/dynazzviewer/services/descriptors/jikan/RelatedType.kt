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
    OTHER("Other", false),
    PREQUEL("Prequel", true),
    SEQUEL("Sequel", true),
    SUMMARY("Summary", false),
    ALTERNATIVE_SETTING("Alternative setting", false),
    FULL_STORY("Full story", true),
}

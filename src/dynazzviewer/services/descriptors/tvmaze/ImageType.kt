package dynazzviewer.services.descriptors.tvmaze

import com.fasterxml.jackson.annotation.JsonValue

enum class ImageType(
    @JsonValue
       private val value: String
) {
    MEDIUM("medium"),
    ORIGINAL("original"),
}

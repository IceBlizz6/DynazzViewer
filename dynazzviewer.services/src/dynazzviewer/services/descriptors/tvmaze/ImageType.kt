package dynazzviewer.services.descriptors.tvmaze

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ImageType {
    @SerialName("medium")
    MEDIUM,

    @SerialName("original")
    ORIGINAL,
}

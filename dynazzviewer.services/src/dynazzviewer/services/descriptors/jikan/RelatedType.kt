package dynazzviewer.services.descriptors.jikan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RelatedType(
    val includeInBatch: Boolean
) {
    @SerialName("Adaptation")
    ADAPTATION(false),
    @SerialName("Side story")
    SIDE_STORY(true),
    @SerialName("Parent story")
    PARENT_STORY(true),
    @SerialName("Other")
    OTHER(false),
    @SerialName("Prequel")
    PREQUEL(true),
    @SerialName("Sequel")
    SEQUEL(true),
    @SerialName("Summary")
    SUMMARY(false),
    @SerialName("Alternative setting")
    ALTERNATIVE_SETTING(false),
    @SerialName("Alternative version")
    ALTERNATIVE_VERSION(false),
    @SerialName("Full story")
    FULL_STORY(true),
    @SerialName("Spin-off")
    SPIN_OFF(false),
    @SerialName("Character")
    CHARACTER(false)
}

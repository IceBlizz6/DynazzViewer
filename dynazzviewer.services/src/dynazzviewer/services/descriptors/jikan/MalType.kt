package dynazzviewer.services.descriptors.jikan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MalType {
    @SerialName("TV")
    TV,

    @SerialName("anime")
    ANIME,

    @SerialName("manga")
    MANGA,

    @SerialName("ONA")
    ONA,

    @SerialName("OVA")
    OVA,

    @SerialName("Movie")
    MOVIE,

    @SerialName("Special")
    SPECIAL,

    @SerialName("-")
    UNKNOWN,

    @SerialName("Music")
    MUSIC,
}

package dynazzviewer.services.descriptors.jikan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AnimeShowHeader(
    @SerialName("mal_id")
    val malId: Int,
    val type: MalType,
    val name: String
)

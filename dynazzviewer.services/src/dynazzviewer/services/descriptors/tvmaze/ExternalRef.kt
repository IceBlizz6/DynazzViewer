package dynazzviewer.services.descriptors.tvmaze

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ExternalRef(
    @SerialName("tvrage")
    val tvRage: Int?,
    @SerialName("thetvdb")
    val theTvDb: Int?,
    val imdb: String?
)

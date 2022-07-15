package dynazzviewer.services.descriptors.jikan

import dynazzviewer.services.descriptors.DescriptionPart
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
class AnimeEpisode(
    @SerialName("mal_id")
    val episodeId: Int,
    val title: String,
    @Serializable(LocalDateSerializer::class)
    var aired: LocalDate?,
    val filler: Boolean?,
    val recap: Boolean?
) {
    fun toDescriptionPart(malId: String): DescriptionPart {
        return DescriptionPart(
            index = episodeId,
            name = title,
            aired = aired,
            uniqueKey = "MAL/$malId/$episodeId",
            episodeNumber = episodeId,
        )
    }
}

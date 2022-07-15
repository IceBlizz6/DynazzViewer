package dynazzviewer.services.descriptors.tvmaze

import dynazzviewer.services.descriptors.DescriptionPart
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
class Episode(
    val id: Int,
    val name: String,
    val season: Int,
    @SerialName("number")
    val episode: Int?,
    @SerialName("airdate")
    @Serializable(LocalDateSerializer::class)
    val airDate: LocalDate
) {
    fun toDescriptionPart(): DescriptionPart {
        return DescriptionPart(
            index = episode ?: -1,
            name = name,
            aired = airDate,
            uniqueKey = "TvMaze/$id/$season/$episode",
            episodeNumber = episode
        )
    }
}

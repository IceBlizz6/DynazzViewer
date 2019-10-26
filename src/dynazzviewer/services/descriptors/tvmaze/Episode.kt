package dynazzviewer.services.descriptors.tvmaze

import com.fasterxml.jackson.annotation.JsonProperty
import dynazzviewer.services.descriptors.DescriptionPart
import java.time.LocalDate

class Episode(
    @JsonProperty("id", required = true)
       val id: Int,
    @JsonProperty("name", required = true)
       val name: String,
    @JsonProperty("season", required = true)
       val season: Int,
    @JsonProperty("number", required = true)
       val episode: Int,
    @JsonProperty("airdate", required = true)
       val airDate: LocalDate
) {
    fun toDescriptionPart(): DescriptionPart {
        return DescriptionPart(
            index = episode,
            name = name,
            aired = airDate,
            uniqueKey = "TvMaze/$id/$season/$episode",
            episodeNumber = episode
        )
    }
}

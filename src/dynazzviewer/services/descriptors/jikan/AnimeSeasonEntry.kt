package dynazzviewer.services.descriptors.jikan

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

class AnimeSeasonEntry(
    @JsonProperty("mal_id")
       val malId: Int,
    @JsonProperty("url")
       val url: String,
    @JsonProperty("title")
       val title: String,
    @JsonProperty("image_url")
       val imageUrl: String,
    @JsonProperty("type")
       val type: MalType,
    @JsonProperty("airing_start")
       @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[xxx]")
       val airingStart: LocalDateTime,
    @JsonProperty("episodes")
       val episodes: Int,
    @JsonProperty("score")
       val score: Double,
    @JsonProperty("r18")
       val adult: Boolean,
    @JsonProperty("kids")
       val kids: Boolean
)

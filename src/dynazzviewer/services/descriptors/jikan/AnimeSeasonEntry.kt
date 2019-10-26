package dynazzviewer.services.descriptors.jikan

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

class AnimeSeasonEntry(
    @JsonProperty("mal_id", required = true)
       val malId: Int,
    @JsonProperty("url", required = true)
       val url: String,
    @JsonProperty("title", required = true)
       val title: String,
    @JsonProperty("image_url", required = true)
       val imageUrl: String,
    @JsonProperty("type", required = true)
       val type: MalType,
    @JsonProperty("airing_start", required = true)
       @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[xxx]")
       val airingStart: LocalDateTime,
    @JsonProperty("episodes", required = true)
       val episodes: Int,
    @JsonProperty("score", required = true)
       val score: Double,
    @JsonProperty("r18", required = true)
       val adult: Boolean,
    @JsonProperty("kids", required = true)
       val kids: Boolean
)

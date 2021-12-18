package dynazzviewer.services.descriptors.jikan

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import dynazzviewer.base.ExtDatabase
import dynazzviewer.services.descriptors.ResultHeader
import java.time.LocalDateTime

class ResultItem(
    @JsonProperty("mal_id", required = true)
    val malId: Int,
    @JsonProperty("url", required = true)
    val url: String,
    @JsonProperty("image_url", required = true)
    val imageUrl: String,
    @JsonProperty("title", required = true)
    val title: String,
    @JsonProperty("airing", required = true)
    val airing: Boolean,
    @JsonProperty("type", required = true)
    val type: MalType,
    @JsonProperty("episodes", required = true)
    val episodes: Int,
    @JsonProperty("score", required = true)
    val score: Double,
    @JsonProperty("start_date", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[xxx]")
    val startDate: LocalDateTime?,
    @JsonProperty("end_date", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[xxx]")
    val endDate: LocalDateTime?,
    @JsonProperty("rated", required = true)
    val rated: String?
) {
    fun toResultHeader(): ResultHeader {
        return ResultHeader(
            name = title,
            imageUrl = imageUrl,
            extDb = ExtDatabase.MyAnimeList,
            extDbCode = malId.toString()
        )
    }
}

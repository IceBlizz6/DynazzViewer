package dynazzviewer.services.descriptors.jikan

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import dynazzviewer.base.ExtDatabase
import dynazzviewer.services.descriptors.DescriptionPart
import java.time.LocalDateTime

class Episode(
    @JsonProperty("episode_id", required = true)
    val episodeId: Int,
    @JsonProperty("title", required = true)
    val title: String,
    @JsonProperty("title_japanese", required = true)
    val japaneseTitle: String? = null,
    @JsonProperty("title_romanji", required = true)
    val romanjiTitle: String? = null,
    @JsonProperty("aired", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[xxx]")
    var aired: LocalDateTime? = null,
    @JsonProperty("filler", required = true)
    val filler: Boolean? = null,
    @JsonProperty("recap", required = true)
    val recap: Boolean? = null,
    @JsonProperty("video_url", required = true)
    val videoUrl: String? = null,
    @JsonProperty("forum_url", required = true)
    val forumUrl: String? = null
) {
    fun toDescriptionPart(malId: String): DescriptionPart {
        return DescriptionPart(
            index = episodeId,
            name = title,
            aired = aired?.toLocalDate(),
            uniqueKey = "MAL/$malId/$episodeId",
            episodeNumber = episodeId,
            extDatabase = ExtDatabase.MyAnimeList,
            extCode = malId
        )
    }
}

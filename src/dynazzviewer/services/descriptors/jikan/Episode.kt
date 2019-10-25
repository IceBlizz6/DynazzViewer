package dynazzviewer.services.descriptors.jikan

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import dynazzviewer.services.descriptors.DescriptionPart
import java.time.LocalDateTime

class Episode(
    @JsonProperty("episode_id")
       val episodeId: Int,
    @JsonProperty("title")
       val title: String,
    @JsonProperty("title_japanese")
       val japaneseTitle: String? = null,
    @JsonProperty("title_romanji")
       val romanjiTitle: String? = null,
    @JsonProperty("aired")
       @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[xxx]")
       var aired: LocalDateTime? = null,
    @JsonProperty("filler")
       val filler: Boolean? = null,
    @JsonProperty("recap")
       val recap: Boolean? = null,
    @JsonProperty("video_url", required = true)
       val videoUrl: String? = null,
    @JsonProperty("forum_url")
       val forumUrl: String? = null
) {
    fun toDescriptionPart(malId: String): DescriptionPart {
        return DescriptionPart(
            index = episodeId,
            name = title,
            aired = aired?.toLocalDate(),
            uniqueKey = "MAL/$malId/$episodeId"
        )
    }
}

package dynazzviewer.services.descriptors.jikan

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import dynazzviewer.base.ExtDatabase
import dynazzviewer.services.descriptors.DescriptionPartCollection
import java.time.LocalDateTime

class Show(
    @JsonProperty("request_hash", required = true)
       val requestHash: String,
    @JsonProperty("request_cached", required = true)
       val requestCached: Boolean,
    @JsonProperty("request_cache_expiry", required = true)
       val requestCacheExpiry: Int,
    @JsonProperty("mal_id", required = true)
       val malId: Int,
    @JsonProperty("url", required = true)
       val url: String,
    @JsonProperty("image_url", required = true)
       val imageUrl: String,
    @JsonProperty("trailer_url", required = true)
       val trailerUrl: String?,
    @JsonProperty("title", required = true)
       val title: String,
    @JsonProperty("title_english", required = true)
       val englishTitle: String?,
    @JsonProperty("title_japanese", required = true)
       val japaneseTitle: String,
    @JsonProperty("title_synonyms", required = true)
       val synonymTitles: List<String>,
    @JsonProperty("type", required = true)
       val type: MalType,
    @JsonProperty("source", required = true)
       val source: String,
    @JsonProperty("episodes", required = true)
       val episodes: Int,
    @JsonProperty("status", required = true)
       val status: SeriesStatus,
    @JsonProperty("airing", required = true)
       val airing: Boolean,
    @JsonProperty("aired", required = true)
       val aired: Aired,
    @JsonProperty("score", required = true)
       val score: Double,
    @JsonProperty("rank", required = true)
       val rank: Int,
    @JsonProperty("related", required = true)
       val related: Map<RelatedType, List<Related>>,
    @JsonProperty("genres", required = true)
       val genres: List<Genre>
) {
    class Aired(
        @JsonProperty("from", required = true)
           @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[xxx]")
           val from: LocalDateTime,
        @JsonProperty("to", required = true)
           @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[xxx]")
           val to: LocalDateTime
    )
    class Related(
        @JsonProperty("mal_id", required = true)
           val malId: Int,
        @JsonProperty("type", required = true)
           val type: MalType,
        @JsonProperty("name", required = true)
           val name: String,
        @JsonProperty("url", required = true)
           val url: String
    )
    class Genre(
        @JsonProperty("mal_id", required = true)
           val malId: Int,
        @JsonProperty("type", required = true)
           val type: String,
        @JsonProperty("name", required = true)
           val name: String,
        @JsonProperty("url", required = true)
           val url: String
    )
    fun toDescriptionPartCollection(episodes: List<Episode>): DescriptionPartCollection {
        return DescriptionPartCollection(
            name = title,
            extDatabaseCode = malId.toString(),
            extDatabase = ExtDatabase.MyAnimeList,
            episodes = episodes.map { e -> e.toDescriptionPart(malId = malId.toString()) },
            uniqueKey = "MAL/$malId",
            sortOrder = aired.from.year * 400 + aired.from.dayOfYear,
            seasonNumber = null
        )
    }
}

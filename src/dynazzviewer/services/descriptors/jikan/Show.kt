package dynazzviewer.services.descriptors.jikan

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import dynazzviewer.base.ExtDatabase
import dynazzviewer.services.descriptors.DescriptionPartCollection
import java.time.LocalDateTime

class Show(
	@JsonProperty("request_hash")
	val requestHash : String,
	@JsonProperty("request_cached")
	val requestCached : Boolean,
	@JsonProperty("request_cache_expiry")
	val requestCacheExpiry : Int,
	@JsonProperty("mal_id")
	val malId : Int,
	@JsonProperty("url")
	val url : String,
	@JsonProperty("image_url")
	val imageUrl : String,
	@JsonProperty("trailer_url", required = true)
	val trailerUrl : String?,
	@JsonProperty("title")
	val title : String,
	@JsonProperty("title_english")
	val englishTitle : String?,
	@JsonProperty("title_japanese")
	val japaneseTitle : String,
	@JsonProperty("title_synonyms")
	val synonymTitles : List<String>,
	@JsonProperty("type")
	val type : MalType,
	@JsonProperty("source")
	val source : String,
	@JsonProperty("episodes")
	val episodes : Int,
	@JsonProperty("status")
	val status : SeriesStatus,
	@JsonProperty("airing")
	val airing : Boolean,
	@JsonProperty("aired")
	val aired : Aired,
	@JsonProperty("score")
	val score : Double,
	@JsonProperty("rank")
	val rank : Int,
	@JsonProperty("related")
	val related : Map<RelatedType, List<Related>>,
	@JsonProperty("genres")
	val genres : List<Genre>
) {
	class Aired(
		@JsonProperty("from")
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[xxx]")
		val from : LocalDateTime,
		@JsonProperty("to")
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[xxx]")
		val to : LocalDateTime
	)
	class Related(
		@JsonProperty("mal_id")
		val malId : Int,
		@JsonProperty("type")
		val type : MalType,
		@JsonProperty("name")
		val name : String,
		@JsonProperty("url")
		val url : String
	)
	class Genre(
		@JsonProperty("mal_id")
		val malId : Int,
		@JsonProperty("type")
		val type : String,
		@JsonProperty("name")
		val name : String,
		@JsonProperty("url")
		val url : String
	)
	fun toDescriptionPartCollection(episodes : List<Episode>) : DescriptionPartCollection {
		return DescriptionPartCollection(
			name = title,
			extDatabaseCode = malId.toString(),
			extDatabase = ExtDatabase.MyAnimeList,
			episodes = episodes.map { e -> e.toDescriptionPart(malId = malId.toString()) },
			uniqueKey = "MAL/$malId",
			sortOrder = aired.from.year * 400 + aired.from.dayOfYear
		)
	}
}

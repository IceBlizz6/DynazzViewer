package dynazzviewer.services.descriptors.jikan

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import dynazzviewer.base.ExtDatabase
import dynazzviewer.services.descriptors.ResultHeader
import java.time.LocalDateTime

class ResultItem(
	@JsonProperty("mal_id")
	val malId : Int,
	@JsonProperty("url")
	val url : String,
	@JsonProperty("image_url")
	val imageUrl : String,
	@JsonProperty("title")
	val title : String,
	@JsonProperty("airing")
	val airing : Boolean,
	@JsonProperty("type")
	val type : MalType,
	@JsonProperty("episodes")
	val episodes : Int,
	@JsonProperty("score")
	val score : Double,
	@JsonProperty("start_date")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[xxx]")
	val startDate : LocalDateTime?,
	@JsonProperty("end_date")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[xxx]")
	val endDate : LocalDateTime?,
	@JsonProperty("rated")
	val rated : String?
) {
	fun toResultHeader() : ResultHeader {
		return ResultHeader(
			name = title,
			imageUrl = imageUrl,
			extDb = ExtDatabase.MyAnimeList,
			extDbCode = malId.toString()
		)
	}
}

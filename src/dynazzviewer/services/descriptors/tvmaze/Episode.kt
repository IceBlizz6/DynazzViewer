package dynazzviewer.services.descriptors.tvmaze

import com.fasterxml.jackson.annotation.JsonProperty
import dynazzviewer.services.descriptors.DescriptionPart
import java.time.LocalDate

class Episode(
	@JsonProperty("id")
	val id : Int,
	@JsonProperty("name")
	val name : String,
	@JsonProperty("season")
	val season : Int,
	@JsonProperty("episode")
	val episode : Int,
	@JsonProperty("airdate")
	val airDate : LocalDate
) {
	fun toDescriptionPart() : DescriptionPart {
		return DescriptionPart(
			index = episode,
			name = name,
			aired = airDate,
			uniqueKey = "TvMaze/$id/$season/$episode"
		)
	}
}

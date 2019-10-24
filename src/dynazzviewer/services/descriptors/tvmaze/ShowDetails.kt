package dynazzviewer.services.descriptors.tvmaze

import com.fasterxml.jackson.annotation.JsonProperty
import dynazzviewer.services.descriptors.DescriptionPart
import dynazzviewer.services.descriptors.DescriptionPartCollection
import dynazzviewer.services.descriptors.DescriptionUnit
import javassist.runtime.Desc
import java.time.LocalDate

class ShowDetails(
	@JsonProperty("id")
	val id : Int,
	@JsonProperty("name")
	val name : String,
	@JsonProperty("genres")
	val genres : List<String>,
	@JsonProperty("status")
	val status : String,
	@JsonProperty("premiered")
	val premiered : LocalDate,
	@JsonProperty("externals")
	val externals : Map<String, String?>,
	@JsonProperty("image")
	val images : Map<ImageType, String>
) {
	fun toDescriptionUnit(episodes: List<Episode>) : DescriptionUnit {
		val seasonList = mutableListOf<DescriptionPartCollection>()
		val seasonNumbers : List<Int> = episodes.map { e -> e.season }.distinct()
		for (seasonNumber in seasonNumbers) {
			val parts : List<DescriptionPart> = episodes
				.filter { e -> e.season == seasonNumber }
				.map { e -> e.toDescriptionPart() }
			seasonList.add(
				DescriptionPartCollection(
					episodes = parts,
					extDatabase = null,
					extDatabaseCode = null,
					name = "Season $seasonNumber",
					uniqueKey = "TvMaze/$id/$seasonNumber",
					sortOrder = seasonNumber
				)
			)
		}
		val imageUrl = images[ImageType.MEDIUM]!!
		return DescriptionUnit(
			name = name,
			children = seasonList,
			imageUrls = listOf(imageUrl),
			uniqueKey = "TvMaze/$id",
			tags = genres
		)
	}
}

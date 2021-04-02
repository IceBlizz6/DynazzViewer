package dynazzviewer.services.descriptors.tvmaze

import com.fasterxml.jackson.annotation.JsonProperty
import dynazzviewer.services.descriptors.DescriptionPart
import dynazzviewer.services.descriptors.DescriptionPartCollection
import dynazzviewer.services.descriptors.DescriptionUnit
import java.time.LocalDate

class ShowDetails(
    @JsonProperty("id", required = true)
    val id: Int,
    @JsonProperty("name", required = true)
    val name: String,
    @JsonProperty("genres", required = true)
    val genres: List<String>,
    @JsonProperty("status", required = true)
    val status: String,
    @JsonProperty("premiered", required = true)
    val premiered: LocalDate,
    @JsonProperty("externals", required = true)
    val externals: Map<String, String?>,
    @JsonProperty("image", required = true)
    val images: Map<ImageType, String>
) {
    fun toDescriptionUnit(episodes: List<Episode>): DescriptionUnit {
        val seasonList = mutableListOf<DescriptionPartCollection>()
        val seasonNumbers: List<Int> = episodes.map { e -> e.season }.distinct()
        for (seasonNumber in seasonNumbers) {
            val parts: List<DescriptionPart> = episodes
                .filter { e -> e.season == seasonNumber }
                .map { e -> e.toDescriptionPart() }
            seasonList.add(
                DescriptionPartCollection(
                    episodes = parts,
                    extDatabase = null,
                    extDatabaseCode = null,
                    name = "Season $seasonNumber",
                    uniqueKey = "TvMaze/$id/$seasonNumber",
                    sortOrder = seasonNumber,
                    seasonNumber = seasonNumber,
                    alternativeTitles = null
                )
            )
        }
        val imageUrl = images[ImageType.MEDIUM]!!
        return DescriptionUnit(
            name = name,
            children = seasonList,
            imageUrls = setOf(imageUrl),
            uniqueKey = "TvMaze/$id",
            tags = genres.toSet()
        )
    }
}

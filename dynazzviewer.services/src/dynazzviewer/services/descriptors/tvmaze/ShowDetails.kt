package dynazzviewer.services.descriptors.tvmaze

import dynazzviewer.entities.ExtDatabase
import dynazzviewer.services.descriptors.DescriptionPart
import dynazzviewer.services.descriptors.DescriptionPartCollection
import dynazzviewer.services.descriptors.DescriptionUnit
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
class ShowDetails(
    val id: Int,
    val name: String,
    val genres: List<String>,
    val status: String,
    @Serializable(LocalDateSerializer::class)
    val premiered: LocalDate,
    val externals: ExternalRef,
    val image: Map<ImageType, String>?
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
        return DescriptionUnit(
            name = name,
            children = seasonList,
            imageUrls = image?.get(ImageType.MEDIUM)?.let { setOf(it) } ?: emptySet(),
            uniqueKey = "TvMaze/$id",
            tags = genres.toSet(),
            extDatabase = ExtDatabase.TvMaze,
            extDatabaseCode = id.toString()
        )
    }
}

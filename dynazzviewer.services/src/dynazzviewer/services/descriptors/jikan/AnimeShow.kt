package dynazzviewer.services.descriptors.jikan

import dynazzviewer.entities.ExtDatabase
import dynazzviewer.services.descriptors.DescriptionPartCollection
import dynazzviewer.services.descriptors.ResultHeader
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
class AnimeShow(
    @SerialName("mal_id")
    val malId: Int,
    @SerialName("url")
    val url: String,
    @SerialName("title")
    val title: String,
    @SerialName("title_english")
    val englishTitle: String?,
    @SerialName("title_japanese")
    val japaneseTitle: String?,
    @SerialName("title_synonyms")
    val synonymTitles: List<String>,
    @SerialName("episodes")
    val episodes: Int?,
    @SerialName("images")
    val images: Images,
    @SerialName("type")
    val type: MalType,
    @SerialName("score")
    val score: Double?,
    @SerialName("aired")
    val aired: Dates,
    val genres: List<AnimeGenre>,
    val relations: List<AnimeRelation>? = null
) {
    @Serializable
    class Images(
        val jpg: ImageLinks,
        val webp: ImageLinks
    )

    @Serializable
    class ImageLinks(
        @SerialName("small_image_url")
        val small: String,
        @SerialName("image_url")
        val medium: String,
        @SerialName("large_image_url")
        val large: String
    )

    @Serializable
    class Dates(
        @Serializable(LocalDateSerializer::class)
        val from: LocalDate?,
        @Serializable(LocalDateSerializer::class)
        val to: LocalDate?
    )

    fun toResultHeader(): ResultHeader {
        return ResultHeader(
            name = title,
            imageUrl = images.jpg.medium,
            extDb = ExtDatabase.MyAnimeList,
            extDbCode = malId.toString()
        )
    }

    fun toDescriptionPartCollection(episodes: List<AnimeEpisode>): DescriptionPartCollection {
        var alternativeTitles = listOfNotNull(title, englishTitle, japaneseTitle).toMutableList()
        alternativeTitles.addAll(synonymTitles)

        return DescriptionPartCollection(
            name = title,
            extDatabaseCode = malId.toString(),
            extDatabase = ExtDatabase.MyAnimeList,
            episodes = episodes.map { e -> e.toDescriptionPart(malId = malId.toString()) },
            uniqueKey = "MAL/$malId",
            sortOrder = aired.from?.let { it.year * 400 + it.dayOfYear },
            seasonNumber = null,
            alternativeTitles = alternativeTitles
        )
    }
}

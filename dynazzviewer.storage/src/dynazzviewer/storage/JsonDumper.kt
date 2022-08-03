package dynazzviewer.storage

import dynazzviewer.entities.AnimeSeasonFlagState
import dynazzviewer.entities.ExtDatabase
import dynazzviewer.entities.ViewStatus
import kotlinx.serialization.Serializable
import java.io.File
import java.time.LocalDate

interface JsonDumper {
    fun dumpTo(file: File)

    @Serializable
    class Root(
        val animeSeasonFlags: List<AnimeSeasonFlag>,
        val mediaFiles: List<MediaFile>,
        val mediaLibrary: List<MediaSeries>
    )

    @Serializable
    class AnimeSeasonFlag(
        val malId: Int,
        val flag: AnimeSeasonFlagState
    )

    @Serializable
    class MediaFile(
        val fileName: String,
        val flag: ViewStatus
    )

    @Serializable
    class MediaSeries(
        val name: String,
        val extDb: ExternalDbEntry?,
        val uniqueKey: String?,
        val tags: List<String>,
        val images: List<String>,
        val seasons: List<MediaSeason>
    ) {
        companion object {
            fun from(source: dynazzviewer.entities.MediaUnit): MediaSeries {
                return MediaSeries(
                    name = source.name,
                    extDb = source.databaseEntry?.let {
                        ExternalDbEntry(it.mediaDatabase, it.code)
                    },
                    uniqueKey = source.uniqueKey,
                    tags = source.tags.map { it.name },
                    images = source.images.map { it.url },
                    seasons = source.children.map { MediaSeason.from(it) }
                )
            }
        }
    }

    @Serializable
    class MediaSeason(
        val name: String,
        val extDb: ExternalDbEntry?,
        val sortOrder: Int?,
        val seasonNumber: Int?,
        val alternativeTitles: List<String>,
        val episodes: List<MediaEpisode>
    ) {
        companion object {
            fun from(source: dynazzviewer.entities.MediaPartCollection): MediaSeason {
                return MediaSeason(
                    name = source.name,
                    extDb = source.databaseEntry?.let {
                        ExternalDbEntry(it.mediaDatabase, it.code)
                    },
                    sortOrder = source.sortOrder,
                    seasonNumber = source.seasonNumber,
                    alternativeTitles = source.alternativeTitles.map { it.name },
                    episodes = source.children.map { MediaEpisode.from(it) }
                )
            }
        }
    }

    @Serializable
    class MediaEpisode(
        val name: String,
        val sortOrder: Int?,
        @Serializable(LocalDateSerializer::class)
        val aired: LocalDate?,
        val episodeNumber: Int?,
        val flag: ViewStatus,
        val mediaFilePath: String?
    ) {
        companion object {
            fun from(source: dynazzviewer.entities.MediaPart): MediaEpisode {
                return MediaEpisode(
                    name = source.name,
                    sortOrder = source.sortOrder,
                    aired = source.aired,
                    episodeNumber = source.episodeNumber,
                    flag = source.status,
                    mediaFilePath = source.mediaFile?.name
                )
            }
        }
    }

    @Serializable
    class ExternalDbEntry(
        val db: ExtDatabase,
        val code: String
    )
}

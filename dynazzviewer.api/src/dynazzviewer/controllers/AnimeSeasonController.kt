package dynazzviewer.controllers

import dynazzviewer.entities.AnimeSeasonFlagState
import dynazzviewer.entities.ExtDatabase
import dynazzviewer.files.FileConfiguration
import dynazzviewer.services.descriptors.jikan.JikanApi
import dynazzviewer.services.descriptors.jikan.MalType
import dynazzviewer.services.descriptors.jikan.MalYearSeason
import dynazzviewer.storage.MediaIdentity
import dynazzviewer.storage.Storage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDate

class AnimeSeasonController(
    val storage: Storage,
    val config: FileConfiguration,
    val api: JikanApi
) {
    val cachePath = config.cacheDirectoryPath + "/anime/"

    init {
        val root = File(cachePath)
        if (!root.exists()) {
            root.mkdir()
        }
    }

    fun addAnimeSeason(year: Int, season: MalYearSeason) {
        val file = path(year, season)
        val response = api.season(year, season)
        val json = Json.encodeToString(response)
        file.writeText(json)
    }

    class AnimeMediaIdentity(
        override val extDbCode: String
    ) : MediaIdentity {
        override val extDb: ExtDatabase
            get() = ExtDatabase.MyAnimeList
    }

    fun load(year: Int, season: MalYearSeason): List<AnimeSeasonSeries> {
        val file = path(year, season)
        val json = file.readText()
        val series = api.seasonFromJson(json)
        return storage.read { context ->
            val storedFlags = context.animeSeasonSeries(series.map { it.malId })
            val mediaIds = series.map { AnimeMediaIdentity(it.malId.toString()) }
            val cacheExists = context.mediaUnitExist(mediaIds)
            series.map {
                AnimeSeasonSeries(
                    title = it.title,
                    imageUrl = it.images.jpg.medium,
                    type = it.type,
                    airingStart = it.aired.from,
                    episodes = it.episodes,
                    malId = it.malId,
                    score = it.score,
                    url = it.url,
                    flag = storedFlags[it.malId] ?: AnimeSeasonFlagState.None,
                    saved = cacheExists[
                        mediaIds.single { media -> media.extDbCode == it.malId.toString() }
                    ]!!
                )
            }
        }
    }

    fun list(): List<MalSeasonIdentifier> {
        return File(cachePath)
            .listFiles()
            .map { parsePath(it.name) }
    }

    fun markSeries(malId: Int, flag: AnimeSeasonFlagState) {
        storage.readWrite { context ->
            context.setAnimeSeasonFlag(malId, flag)
        }
    }

    private fun path(year: Int, season: MalYearSeason): File {
        val fileName = "y-$year-${season.seasonName}.json"
        return File(cachePath + fileName)
    }

    private fun parsePath(fileName: String): MalSeasonIdentifier {
        val items = fileName.split("-", ".")
        val year: Int = items[1].toInt()
        val season: MalYearSeason = MalYearSeason.values().single { it.seasonName == items[2] }
        return MalSeasonIdentifier(year, season)
    }

    class MalSeasonIdentifier(
        val year: Int,
        val season: MalYearSeason
    )

    class AnimeSeasonSeries(
        val malId: Int,
        val url: String,
        val title: String,
        val imageUrl: String,
        val type: MalType,
        val airingStart: LocalDate?,
        val episodes: Int?,
        val score: Double?,
        val flag: AnimeSeasonFlagState,
        val saved: Boolean
    )
}

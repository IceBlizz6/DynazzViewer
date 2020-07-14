package dynazzviewer.controllers

import dynazzviewer.base.AnimeSeasonFlagState
import dynazzviewer.filesystem.FileConfiguration
import dynazzviewer.services.descriptors.jikan.JikanApi
import dynazzviewer.services.descriptors.jikan.MalType
import dynazzviewer.services.descriptors.jikan.MalYearSeason
import dynazzviewer.storage.Storage
import java.io.File
import java.time.LocalDateTime

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
        val response = api.seasonWithResponse(year, season)
        val json = response.jsonContent
        file.writeText(json)
    }

    fun load(year: Int, season: MalYearSeason): List<AnimeSeasonSeries> {
        val file = path(year, season)
        val json = file.readText()
        val series = api.parseSeason(json)

        storage.read().use { context ->
            val storedFlags = context.animeSeasonSeries(series.map { it.malId })
            return series.map {
                AnimeSeasonSeries(
                    title = it.title,
                    imageUrl = it.imageUrl,
                    type = it.type,
                    airingStart = it.airingStart,
                    episodes = it.episodes,
                    malId = it.malId,
                    score = it.score,
                    url = it.url,
                    flag = storedFlags[it.malId] ?: AnimeSeasonFlagState.None
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
        storage.readWrite().use { context ->
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
        val airingStart: LocalDateTime,
        val episodes: Int,
        val score: Double,
        val flag: AnimeSeasonFlagState
    )
}

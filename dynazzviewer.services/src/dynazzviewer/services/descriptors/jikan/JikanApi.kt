package dynazzviewer.services.descriptors.jikan

import dynazzviewer.entities.ExtDatabase
import dynazzviewer.services.WebClient
import dynazzviewer.services.descriptors.DescriptionUnit
import dynazzviewer.services.descriptors.DescriptorApi
import dynazzviewer.services.descriptors.ResultHeader
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.LocalDate

class JikanApi(
    private val webClient: WebClient,
    private val fetchRelated: Boolean = true,
    private val autoFillEpisodes: Boolean = true,
    private val autoFillEpisodeAirDates: Boolean = true
) : DescriptorApi {
    companion object {
        private const val EPISODE_AIR_DAYS_INTERVAL = 7
    }

    private val parser = Json { ignoreUnknownKeys = true }

    override fun querySearch(db: ExtDatabase, name: String): List<ResultHeader>? {
        return if (db == ExtDatabase.MyAnimeList) {
            search(name).map { e -> e.toResultHeader() }
        } else {
            null
        }
    }

    override fun queryLookup(db: ExtDatabase, code: String): DescriptionUnit? {
        if (ExtDatabase.MyAnimeList == db) {
            val jikanSeries = fetchSeries(code)
            val partCollections = jikanSeries
                .map { e -> e.toDescriptionPartCollection() }
            val firstName = jikanSeries
                .map { e -> e.show }
                .filter { it.aired.from != null }
                .minByOrNull { it.aired.from!! }
                ?.title
            return DescriptionUnit(
                children = partCollections,
                uniqueKey = null,
                name = firstName ?: jikanSeries.first().show.title,
                tags = jikanSeries
                    .flatMap { e -> e.show.genres.map { genre -> genre.name } }
                    .toSet(),
                imageUrls = jikanSeries
                    .map { e -> e.show.images.jpg.medium }
                    .toSet()
            )
        } else {
            return null
        }
    }

    private fun fetchSeries(code: String): List<JikanSeries> {
        return if (fetchRelated) {
            fetchAllRelatedSeries(code.toInt())
        } else {
            listOf(fetchSingleSeries(code.toInt()))
        }
    }

    private fun autoFillEpisodes(episodes: List<AnimeEpisode>, numberOfEpisodes: Int): List<AnimeEpisode> {
        if (episodes.count() != numberOfEpisodes) {
            val mutableEpisodes = mutableListOf<AnimeEpisode>()
            mutableEpisodes.addAll(episodes)
            for (index in 1..numberOfEpisodes) {
                val exists = episodes.any { e -> e.episodeId == index }
                if (!exists) {
                    val autoFill = AnimeEpisode(
                        episodeId = index,
                        title = "Episode $index",
                        aired = null,
                        filler = null,
                        recap = null
                    )
                    mutableEpisodes.add(autoFill)
                }
            }
            return mutableEpisodes
        } else {
            return episodes
        }
    }

    private fun autoFillEpisodeAirDates(
        episodes: List<AnimeEpisode>,
        start: LocalDate,
        end: LocalDate
    ) {
        val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(start, end)
        val weeksDuration = daysBetween / EPISODE_AIR_DAYS_INTERVAL + 1
        val numberOfEpisodes = episodes.count()
        if (weeksDuration == numberOfEpisodes.toLong() &&
            daysBetween % EPISODE_AIR_DAYS_INTERVAL == 0L
        ) {
            for (episode in episodes) {
                val daysAfter = EPISODE_AIR_DAYS_INTERVAL * (episode.episodeId - 1)
                episode.aired = start.plusDays(daysAfter.toLong())
            }
        }
    }

    private fun fetchAllRelatedSeries(code: Int): List<JikanSeries> {
        return lookup(code, mutableSetOf(code))
    }

    private fun lookup(code: Int, completedMalIds: MutableSet<Int>): List<JikanSeries> {
        val seriesData = series(code)
        val episodes = if (autoFillEpisodes && seriesData.episodes != null) {
            autoFillEpisodes(episodes(code), seriesData.episodes)
        } else {
            episodes(code)
        }
        if (autoFillEpisodeAirDates &&
            seriesData.aired.from != null &&
            seriesData.aired.to != null
        ) {
            autoFillEpisodeAirDates(
                episodes,
                seriesData.aired.from,
                seriesData.aired.to
            )
        }
        val relatedList = related(seriesData.malId)
            .filter { e -> e.relation.includeInBatch }
            .map { e -> e.entry }
            .flatMap { e -> e.toList() }
            .map { e -> e.malId }
        val desc = JikanSeries(seriesData, episodes)
        val returnList = mutableListOf(desc)
        for (related in relatedList) {
            if (!completedMalIds.contains(related)) {
                completedMalIds.add(related)
                val children = lookup(related, completedMalIds)
                returnList.addAll(children)
            }
        }
        return returnList
    }

    private fun fetchSingleSeries(code: Int): JikanSeries {
        val seriesData = series(code)
        val episodes = if (autoFillEpisodes && seriesData.episodes != null) {
            autoFillEpisodes(episodes(code), seriesData.episodes)
        } else {
            episodes(code)
        }
        if (autoFillEpisodeAirDates &&
            seriesData.aired.from != null &&
            seriesData.aired.to != null
        ) {
            autoFillEpisodeAirDates(
                episodes,
                seriesData.aired.from,
                seriesData.aired.to
            )
        }
        return JikanSeries(seriesData, episodes)
    }

    private inline fun <reified T> fetchSingle(path: String): T {
        return makeRequest<T>(path).single()
    }

    private inline fun <reified T> fetchListOf(path: String): List<T> {
        return makeRequest<List<T>>(path).flatten()
    }

    private inline fun <reified T> makeRequest(path: String): List<T> {
        val firstResponse = doRequest<T>(url(path, null))
        val list = mutableListOf<T>()
        list.add(firstResponse.data)
        val firstPagination = firstResponse.pagination
        if (firstPagination != null) {
            var nextPage = 2
            var hasNext = firstPagination.hasNextPage
            while (hasNext) {
                val response = doRequest<T>(url(path, nextPage))
                list.add(response.data)
                nextPage += 1
                hasNext = response.pagination!!.hasNextPage
            }
        }
        return list
    }

    private inline fun <reified T> doRequest(fullPath: String): JikanResponse<T> {
        val jsonContent = webClient.requestData(fullPath)
        return parser.decodeFromString(jsonContent)
    }

    private fun url(path: String, page: Int?): String {
        val base = "https://api.jikan.moe/v4"
        val builder = org.apache.http.client.utils.URIBuilder(base + path)
        if (page != null) {
            builder.addParameter("page", page.toString())
        }
        return builder.build().toString()
    }

    fun series(malId: Int): AnimeShow {
        return fetchSingle("/anime/$malId/full")
    }

    fun search(searchText: String): List<AnimeShow> {
        return fetchListOf(
            "/anime?q=${searchText.replace(' ', '+')}"
        )
    }

    fun season(year: Int, season: MalYearSeason): List<AnimeShow> {
        return fetchListOf("/seasons/$year/${season.seasonName}")
    }

    fun seasonFromJson(json: String): List<AnimeShow> {
        return parser.decodeFromString(json)
    }

    private fun episodes(malId: Int): List<AnimeEpisode> {
        return fetchListOf<AnimeEpisode>("/anime/$malId/episodes")
    }

    private fun related(malId: Int): List<AnimeRelation> {
        return fetchListOf("/anime/$malId/relations")
    }
}

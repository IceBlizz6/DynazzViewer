package dynazzviewer.services.descriptors.jikan

import dynazzviewer.base.ExtDatabase
import dynazzviewer.services.WebJsonParser
import dynazzviewer.services.descriptors.DescriptionUnit
import dynazzviewer.services.descriptors.DescriptorApi
import dynazzviewer.services.descriptors.ResultHeader
import java.time.LocalDate

class JikanApi(
    private val parser: WebJsonParser,
    private val fetchRelated: Boolean = true,
    private val autoFillEpisodes: Boolean = true,
    private val autoFillEpisodeAirDates: Boolean = true
) : DescriptorApi {
    companion object {
        private const val BASE_URL = "https://api.jikan.moe/v3"
        private const val EPISODE_AIR_DAYS_INTERVAL = 7
    }

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
            val partCollections = jikanSeries.map { e -> e.toDescriptionPartCollection() }
            val firstName = jikanSeries.map { e -> e.show }.minBy { e -> e.aired.from }!!.title
            return DescriptionUnit(
                children = partCollections,
                uniqueKey = null,
                name = firstName,
                tags = jikanSeries.flatMap { e -> e.show.genres.map { genre -> genre.name } },
                imageUrls = jikanSeries.map { e -> e.show.imageUrl }
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

    private fun autoFillEpisodes(episodes: List<Episode>, numberOfEpisodes: Int): List<Episode> {
        if (episodes.count() != numberOfEpisodes) {
            val mutableEpisodes = mutableListOf<Episode>()
            mutableEpisodes.addAll(episodes)
            for (index in 1..numberOfEpisodes) {
                val exists = episodes.filter { e -> e.episodeId == index }.any()
                if (!exists) {
                    val autoFill = Episode(episodeId = index, title = "Episode $index")
                    mutableEpisodes.add(autoFill)
                }
            }
            return mutableEpisodes
        } else {
            return episodes
        }
    }

    private fun autoFillEpisodeAirDates(
        episodes: List<Episode>,
        start: LocalDate,
        end: LocalDate
    ) {
        val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(start, end)
        val weeksDuration = daysBetween / EPISODE_AIR_DAYS_INTERVAL + 1
        val numberOfEpisodes = episodes.count()
        if (weeksDuration == numberOfEpisodes.toLong() &&
            daysBetween % EPISODE_AIR_DAYS_INTERVAL == 0L) {
            for (episode in episodes) {
                val daysAfter = EPISODE_AIR_DAYS_INTERVAL * (episode.episodeId - 1)
                episode.aired = start.plusDays(daysAfter.toLong()).atStartOfDay()
            }
        }
    }

    private fun fetchAllRelatedSeries(code: Int): List<JikanSeries> {
        return lookup(code, mutableSetOf(code))
    }

    private fun lookup(code: Int, completedMalIds: MutableSet<Int>): List<JikanSeries> {
        val seriesData = series(code)
        val episodes = if (autoFillEpisodes) {
            autoFillEpisodes(episodes(code), seriesData.episodes)
        } else {
            episodes(code)
        }
        if (autoFillEpisodeAirDates) {
            autoFillEpisodeAirDates(episodes, seriesData.aired.from.toLocalDate(),
                seriesData.aired.to.toLocalDate())
        }
        val relatedList = seriesData
            .related
            .filter { e -> e.key.includeInBatch }
            .map { e -> e.value }
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
        val episodes = if (autoFillEpisodes) {
            autoFillEpisodes(episodes(code), seriesData.episodes)
        } else {
            episodes(code)
        }
        if (autoFillEpisodeAirDates) {
            autoFillEpisodeAirDates(episodes, seriesData.aired.from.toLocalDate(),
                seriesData.aired.to.toLocalDate())
        }
        return JikanSeries(seriesData, episodes)
    }

    fun episodes(malId: Int): List<Episode> {
        val firstRequest = requestEpisodes(malId, 1)
        if (firstRequest.episodesLastPage > 1) {
            val episodeList: MutableList<Episode> = mutableListOf()
            episodeList.addAll(firstRequest.episodes)
            for (page in 2..firstRequest.episodesLastPage) {
                val request = requestEpisodes(malId, page)
                episodeList.addAll(request.episodes)
            }
            return episodeList
        } else {
            return firstRequest.episodes
        }
    }

    fun series(malId: Int): Show {
        val uri = "$BASE_URL/anime/$malId"
        return parser.parseJsonRequest(uri, Show::class.java)
    }

    fun search(searchText: String): List<ResultItem> {
        val uri = "$BASE_URL/search/anime?q=${searchText.replace(' ', '+')}"
        return parser.parseJsonRequest(uri, SearchResult::class.java).results
    }

    fun season(year: Int, season: MalYearSeason): List<AnimeSeasonEntry> {
        val uri = "$BASE_URL/season/$year/${season.seasonName}"
        return parser.parseJsonRequest(uri, MalSeasonList::class.java).anime
    }

    private fun requestEpisodes(malId: Int, page: Int): EpisodeCollection {
        val uri = "$BASE_URL/anime/$malId/episodes/$page"
        return parser.parseJsonRequest(uri, EpisodeCollection::class.java)
    }
}

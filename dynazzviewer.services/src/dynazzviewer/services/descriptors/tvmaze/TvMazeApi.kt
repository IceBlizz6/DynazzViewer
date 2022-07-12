package dynazzviewer.services.descriptors.tvmaze

import dynazzviewer.entities.ExtDatabase
import dynazzviewer.services.WebClient
import dynazzviewer.services.descriptors.DescriptionUnit
import dynazzviewer.services.descriptors.DescriptorApi
import dynazzviewer.services.descriptors.ResultHeader
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class TvMazeApi(
    private val webClient: WebClient
) : DescriptorApi {
    private val parser = Json { ignoreUnknownKeys = true }

    override fun querySearch(db: ExtDatabase, name: String): List<ResultHeader>? {
        return if (db == ExtDatabase.TvMaze) {
            search(name).map { e -> e.toResultHeader() }
        } else {
            null
        }
    }

    override fun queryLookup(db: ExtDatabase, code: String): DescriptionUnit? {
        if (ExtDatabase.TvMaze == db) {
            val showDetails = showDetails(code)
            val episodes = episodes(code)
            return showDetails.toDescriptionUnit(episodes)
        } else {
            return null
        }
    }

    fun search(name: String): List<SearchShowResult> {
        val queryString = name.replace(' ', '+')
        val uri = "http://api.tvmaze.com/search/shows?q=$queryString"
        val json = webClient.requestData(uri)
        val results = parser.decodeFromString<Array<SearchShowResult>>(json)
        return results.toList()
    }

    fun episodes(tvMazeId: String): List<Episode> {
        val uri = "http://api.tvmaze.com/shows/$tvMazeId/episodes?specials=1"
        val json = webClient.requestData(uri)
        return parser.decodeFromString<Array<Episode>>(json).toList()
    }

    fun showDetails(tvMazeId: String): ShowDetails {
        val uri = "http://api.tvmaze.com/shows/$tvMazeId"
        val json = webClient.requestData(uri)
        return parser.decodeFromString(json)
    }
}

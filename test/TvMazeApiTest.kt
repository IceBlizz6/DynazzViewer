import base.TestWebClient
import dynazzviewer.base.ExtDatabase
import dynazzviewer.services.HttpWebJsonParser
import dynazzviewer.services.WebClient
import dynazzviewer.services.WebJsonParser
import dynazzviewer.services.descriptors.tvmaze.TvMazeApi
import java.time.LocalDate
import org.junit.Assert
import org.junit.Test

class TvMazeApiTest {
    private val client: WebClient = TestWebClient(
        mapOf(
            "http://api.tvmaze.com/search/shows?q=star+trek" to "tvmaze/search_shows.json",
            "http://api.tvmaze.com/shows/7480/episodes?specials=1" to "tvmaze/episodes.json",
            "http://api.tvmaze.com/shows/7480" to "tvmaze/series_details.json"
        )
    )
    private val parser: WebJsonParser = HttpWebJsonParser(client)
    private val api: TvMazeApi = TvMazeApi(parser)

    @Test
    fun querySearchTest() {
        val result = api.querySearch(ExtDatabase.TvMaze, "star trek")
        Assert.assertNotNull(result)
        Assert.assertEquals(10, result!!.count())
    }

    @Test
    fun queryLookupTest() {
        val result = api.queryLookup(ExtDatabase.TvMaze, "7480")
        Assert.assertNotNull(result)
        Assert.assertEquals("Star Trek: Discovery", result!!.name)
        Assert.assertEquals(2, result.children.count())
        val firstSeason = result.children.first()
        Assert.assertEquals(5, firstSeason.episodes[4].episodeNumber)
        Assert.assertEquals(10, firstSeason.episodes[9].episodeNumber)
    }

    @Test
    fun showDetailsTest() {
        val details = api.showDetails("7480")
        Assert.assertEquals("Star Trek: Discovery", details.name)
    }

    @Test
    fun episodesTest() {
        val episodes = api.episodes("7480")
        Assert.assertEquals(29, episodes.count())
    }

    @Test
    fun searchTest() {
        val results = api.search("star trek")
        Assert.assertEquals(10, results.count())
        Assert.assertEquals("Star Trek", results[0].show.name)
        Assert.assertEquals(LocalDate.parse("1966-09-08"), results[0].show.premiered)
    }
}

import base.TestConfiguration
import base.TestWebClient
import dynazzviewer.base.ExtDatabase
import dynazzviewer.controllers.ServiceDescriptorController
import dynazzviewer.controllers.UpdateListener
import dynazzviewer.services.HttpWebJsonParser
import dynazzviewer.services.WebJsonParser
import dynazzviewer.services.descriptors.jikan.JikanApi
import dynazzviewer.services.descriptors.jikan.MalYearSeason
import dynazzviewer.storage.sqlite.SqlLiteStorage
import java.time.LocalDate
import org.junit.Assert
import org.junit.Test

class JikanApiTest {
    @Test
    fun completeSeriesTest() {
        val map = mapOf(
            "https://api.jikan.moe/v3/anime/11757" to
                "jikan/series_11757.json",
            "https://api.jikan.moe/v3/anime/11757/episodes/1" to
                "jikan/episodes_11757.json",
            "https://api.jikan.moe/v3/anime/20021" to
                "jikan/series_20021.json",
            "https://api.jikan.moe/v3/anime/20021/episodes/1" to
                "jikan/episodes_20021.json",
            "https://api.jikan.moe/v3/anime/21881" to
                "jikan/series_21881.json",
            "https://api.jikan.moe/v3/anime/21881/episodes/1" to
                "jikan/episodes_21881.json",
            "https://api.jikan.moe/v3/anime/31765" to
                "jikan/series_31765.json",
            "https://api.jikan.moe/v3/anime/31765/episodes/1" to
                "jikan/episodes_31765.json",
            "https://api.jikan.moe/v3/anime/36474" to
                "jikan/series_36474.json",
            "https://api.jikan.moe/v3/anime/36474/episodes/1" to
                "jikan/episodes_36474.json",
            "https://api.jikan.moe/v3/anime/39597" to
                "jikan/series_39597.json",
            "https://api.jikan.moe/v3/anime/39597/episodes/1" to
                "jikan/episodes_39597.json",
            "https://api.jikan.moe/v3/anime/40489" to
                "jikan/series_40489.json",
            "https://api.jikan.moe/v3/anime/40489/episodes/1" to
                "jikan/episodes_40489.json",
            "https://api.jikan.moe/v3/anime/40540" to
                "jikan/series_40540.json",
            "https://api.jikan.moe/v3/anime/40540/episodes/1" to
                "jikan/episodes_40540.json"
        )
        val client = mockWebClient(map)
        val storage = SqlLiteStorage(TestConfiguration())
        val controller = ServiceDescriptorController(
            descriptorServices = listOf(JikanApi(client)),
            storage = storage,
            listener = MockUpdateListener()
        )
        val series = controller.queryDescriptor(ExtDatabase.MyAnimeList, "11757")
        Assert.assertNotNull(series!!)
        controller.add(series)
        storage.read().use { context ->
            val mediaUnits = context.mediaUnits()
            Assert.assertEquals(1, mediaUnits.count())
            val mediaUnit = mediaUnits.single()
            Assert.assertTrue(mediaUnit.tags.any { it.name == "Action" })
            Assert.assertTrue(mediaUnit.images.any())
            Assert.assertTrue(mediaUnit.id > 0)
            Assert.assertTrue(mediaUnit.children.any())
        }
    }

    @Test
    fun episodesTest() {
        val client = mockWebClient(
            mapOf("https://api.jikan.moe/v3/anime/37744/episodes/1" to "jikan/episodes.json")
        )
        val jikanApi = JikanApi(client)
        val episodes = jikanApi.episodes(37744)
        Assert.assertEquals(12, episodes.count())
        val episodeAirDate = episodes.first().aired!!
        Assert.assertEquals(2019, episodeAirDate.year)
        Assert.assertEquals(7, episodeAirDate.monthValue)
        Assert.assertEquals(10, episodeAirDate.dayOfMonth)
        Assert.assertEquals(1, episodes[0].episodeId)
        Assert.assertEquals(2, episodes[1].episodeId)
    }

    @Test
    fun seriesTest() {
        val client = mockWebClient(
            mapOf("https://api.jikan.moe/v3/anime/37744" to "jikan/series.json")
        )
        val jikanApi = JikanApi(client, fetchRelated = false)
        val series = jikanApi.series(37744)
        Assert.assertEquals(3, series.genres.count())
        Assert.assertEquals("Adventure", series.genres[1].name)
    }

    @Test
    fun searchSeriesTest() {
        val client = mockWebClient(
            mapOf("https://api.jikan.moe/v3/search/anime?q=Isekai" to "jikan/search_series.json")
        )
        val jikanApi = JikanApi(client)
        val results = jikanApi.search("Isekai")
        Assert.assertEquals(2018, results[0].startDate!!.year)
        Assert.assertEquals(2018, results[0].endDate!!.year)
        Assert.assertEquals(50, results.count())
    }

    @Test
    fun seasonTest() {
        val client = mockWebClient(
            mapOf("https://api.jikan.moe/v3/season/2019/summer" to "jikan/season.json")
        )
        val jikanApi = JikanApi(client)
        val seasonSeries = jikanApi.season(2019, MalYearSeason.SUMMER)
        Assert.assertEquals(200, seasonSeries.count())
    }

    @Test
    fun seriesRelatedCollectionTest() {
        val client = mockWebClient(
            mapOf(
                "https://api.jikan.moe/v3/anime/34712" to "jikan/series_34712.json",
                "https://api.jikan.moe/v3/anime/34712/episodes/1" to "jikan/episodes_34712.json",
                "https://api.jikan.moe/v3/anime/37272" to "jikan/series_37272.json",
                "https://api.jikan.moe/v3/anime/37272/episodes/1" to "jikan/episodes_37272.json"
            )
        )
        val jikanApi = JikanApi(client, fetchRelated = true)
        val data = jikanApi.queryLookup(ExtDatabase.MyAnimeList, "34712")
        Assert.assertNotNull(data)
        Assert.assertEquals(2, data!!.children.count())
        Assert.assertTrue(data.children.filter { e -> e.extDatabaseCode == "34712" }.any())
        Assert.assertTrue(data.children.filter { e -> e.extDatabaseCode == "37272" }.any())
    }

    @Test
    fun autoFillEpisodesTest() {
        val client = mockWebClient(
            mapOf(
                "https://api.jikan.moe/v3/anime/37272" to "jikan/series_37272.json",
                "https://api.jikan.moe/v3/anime/37272/episodes/1" to "jikan/episodes_37272.json"
            )
        )
        val jikanApi = JikanApi(client, fetchRelated = false)
        val data = jikanApi.queryLookup(ExtDatabase.MyAnimeList, "37272")
        Assert.assertNotNull(data)
        Assert.assertEquals(1, data!!.children.count())
        val episodes = data.children[0].episodes
        Assert.assertEquals(2, episodes.count())
        Assert.assertEquals(1, episodes[0].episodeNumber)
        Assert.assertEquals(2, episodes[1].episodeNumber)
        val season = data.children[0]
        Assert.assertNotNull(season.alternativeTitles)
        Assert.assertEquals(3, season.alternativeTitles!!.count())
        val titles = season.alternativeTitles!!
        Assert.assertTrue(titles.contains("Kujira no Kora wa Sajou ni Manabu!"))
        Assert.assertTrue(titles.contains("クジラの子らは砂上に学ぶ!"))
        Assert.assertTrue(titles.contains("Kujira no Kora wa Sajou ni Utau Specials"))
    }

    @Test
    fun autoFillEpisodeAirDatesTest() {
        val client = mockWebClient(
            mapOf(
                "https://api.jikan.moe/v3/anime/37744" to "jikan/series.json",
                "https://api.jikan.moe/v3/anime/37744/episodes/1" to "jikan/episodes.json"
            )
        )
        val jikanApi = JikanApi(client, fetchRelated = false)
        val data = jikanApi.queryLookup(ExtDatabase.MyAnimeList, "37744")
        Assert.assertNotNull(data)
        Assert.assertEquals(1, data!!.children.count())
        val episodes = data.children[0].episodes
        Assert.assertEquals(12, episodes.count())
        Assert.assertEquals(LocalDate.parse("2019-07-10"), episodes[0].aired!!)
        Assert.assertNotNull(episodes[9].aired)
        Assert.assertEquals(LocalDate.parse("2019-09-11"), episodes[9].aired!!)
        Assert.assertEquals(LocalDate.parse("2019-09-25"), episodes[11].aired!!)
    }

    private fun mockWebClient(map: Map<String, String>): WebJsonParser {
        return HttpWebJsonParser(TestWebClient(map))
    }

    class MockUpdateListener : UpdateListener {
        override fun updateMediaUnit(id: Int, recursive: Boolean) = Unit

        override fun updateMediaPart(id: Int) = Unit

        override fun updateMediaFile(id: Int) = Unit

        override fun setMediaFileId(name: String, assignedId: Int) = Unit
    }
}

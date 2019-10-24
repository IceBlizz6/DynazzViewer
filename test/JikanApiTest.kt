import base.TestWebClient
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import dynazzviewer.base.ExtDatabase
import dynazzviewer.services.HttpWebJsonParser
import dynazzviewer.services.WebJsonParser
import dynazzviewer.services.descriptors.jikan.JikanApi
import dynazzviewer.services.descriptors.jikan.MalYearSeason
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class JikanApiTest {
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
		//Assert.assertEquals(4, client.callCount)
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
	
	private fun mockWebClient(map : Map<String, String>) : WebJsonParser {
		return HttpWebJsonParser(TestWebClient(map))
	}
}

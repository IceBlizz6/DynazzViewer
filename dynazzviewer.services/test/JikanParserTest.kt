import dynazzviewer.services.WebClient
import dynazzviewer.services.descriptors.jikan.JikanApi
import dynazzviewer.services.descriptors.jikan.MalYearSeason
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

class JikanParserTest {
    @Test
    fun serializeAndDeserializeAnimeSeasonList() {
        val webClient = MockWebClient()
        val api = JikanApi(webClient)
        val data = api.season(2022, MalYearSeason.WINTER)
        Json.encodeToString(data)
    }

    class MockWebClient : WebClient {
        override fun requestData(uri: String): String {
            if (uri == "https://api.jikan.moe/v4/seasons/2022/winter") {
                val resource = JikanParserTest::class.java.classLoader.getResource("anime_season_page1.json")!!
                return resource.readText()
            } else if (uri == "https://api.jikan.moe/v4/seasons/2022/winter?page=2") {
                val resource = JikanParserTest::class.java.classLoader.getResource("anime_season_page2.json")!!
                return resource.readText()
            } else {
                error("$uri not supported")
            }
        }
    }
}

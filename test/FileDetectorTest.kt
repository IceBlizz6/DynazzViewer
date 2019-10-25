import dynazzviewer.filesystem.FileDetector
import dynazzviewer.filesystem.FileNameDetector
import org.junit.Assert
import org.junit.Test

class FileDetectorTest {
    private val fileDetector: FileDetector = FileNameDetector()

    @Test
    fun parseAnimeEpisode() {
        assertMatch(
            "[HorribleSubs] DanMachi S2 - 08 [720p].mkv",
            "DanMachi S2",
            null,
            8
        )
        assertMatch(
            "[HorribleSubs] Isekai Cheat Magician - 02 [720p].mkv",
            "Isekai Cheat Magician",
            null,
            2
        )
        assertMatch(
            "[HorribleSubs] Okaa-san Online - 01 [720p].mkv",
            "Okaa-san Online",
            null,
            1
        )
        assertMatch(
            "[DameDesuYo] Cop Craft - 02 (1280x720 10bit AAC) [935E63CC].mkv",
            "Cop Craft",
            null,
            2
        )
        assertMatch(
            "[HorribleSubs] UchiMusume - 02 [720p].mkv",
            "UchiMusume",
            null,
            2
        )
        assertMatch(
            "[GJM] Ascendance of a Bookworm (Honzuki no Gekokujou) - 04 [B1C7BCAA].mkv",
            "Ascendance of a Bookworm (Honzuki no Gekokujou)",
            null,
            4
        )
        assertMatch(
            "[PAS] Beastars - 02v2 (WEB 1080p AAC) [BC75A96C].mkv",
            "Beastars",
            null,
            2
        )
        assertMatch(
            "[Commie] Chihayafuru 3 - 11 [0852820A].mkv",
            "Chihayafuru 3",
            null,
            11
        )
        assertMatch(
            "[Chyuu] Nanatsu no Taizai - Kamigami no Gekirin - 02 [1080p][7626ADDB].mkv",
            "Nanatsu no Taizai - Kamigami no Gekirin",
            null,
            2
        )
        assertMatch(
            "[Foxtrot] Vinland Saga - 14 [01B5756D].mkv",
            "Vinland Saga",
            null,
            14
        )
        assertMatch(
            "[DameDesuYo] Domestic na Kanojo - 09v2 (1920x1080 10bit AAC) [CA4C9C35].mkv",
            "Domestic na Kanojo",
            null,
            9
        )
        assertMatch(
            "[DameDesuYo] Shingeki no Kyojin (Season 3) - 39 (1920x1080 10bit AAC) [2546242F].mkv",
            "Shingeki no Kyojin (Season 3)",
            null,
            39
        )
        assertMatch(
            "[DameDesuYo] Wotaku ni Koi wa Muzukashii (Wotakoi) - 02 " +
                    "(1920x1080 10bit AAC) [871836B2].mkv",
            "Wotaku ni Koi wa Muzukashii (Wotakoi)",
            null,
            2
        )
        assertMatch(
            "[DameDesuYo] Garo - Vanishing Line - 21 (1920x1080 10bit AAC) [D99ADBA3].mkv",
            "Garo - Vanishing Line",
            null,
            21
        )
        assertMatch(
            "[DameDesuYo] DanMachi - Sword Oratoria - 01 (1920x1080 10bit AAC) [FEA77661].mkv",
            "DanMachi - Sword Oratoria",
            null,
            1
        )
    }

    @Test
    fun parseWesternEpisode() {
        assertMatch(
            "Star Trek Discovery S01E05.mp4",
            "Star Trek Discovery",
            1,
            5
        )
        assertMatch(
            "Star.Trek.Discovery.S01E02.Battle.at.the.Binary.Stars." +
                    "1080p.WEBRip.6CH.x265.HEVC-PSA.mkv",
            "Star Trek Discovery",
            1,
            2
        )
        assertMatch(
            "Star.Trek.Discovery.S02E14.Such.Sweet.Sorrow.2.720p." +
                    "AMZN.WEB-DL.DD+5.1.H.264-AJP69[ettv].mkv",
            "Star Trek Discovery",
            2,
            14
        )
    }

    private fun assertMatch(fileName: String, seriesName: String, season: Int?, episode: Int?) {
        val result = fileDetector.parse(fileName)
        Assert.assertNotNull(result)
        Assert.assertEquals(seriesName, result!!.name)
        Assert.assertEquals(season, result.season)
        Assert.assertEquals(episode, result.episode)
    }
}

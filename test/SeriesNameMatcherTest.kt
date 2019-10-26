import base.TestConfiguration
import dynazzviewer.entities.AlternativeTitle
import dynazzviewer.entities.MediaPart
import dynazzviewer.entities.MediaPartCollection
import dynazzviewer.entities.MediaUnit
import dynazzviewer.filesystem.SeriesNameMatcher
import dynazzviewer.storage.ReadWriteOperation
import dynazzviewer.storage.Storage
import dynazzviewer.storage.sqlite.SqlLiteStorage
import org.junit.Assert
import org.junit.Test

class SeriesNameMatcherTest {
    private val storage: Storage = SqlLiteStorage(TestConfiguration())
    private var uniqueGenerator: Int = 0

    init {
        storage.readWrite().use { context ->
            createDummy(context, listOf(
                "Honzuki no Gekokujou: Shisho ni Naru Tame ni wa Shudan wo Erandeiraremasen",
                "Ascendance of a Bookworm"
            ))
            createDummy(context, listOf(
                "Nanatsu no Taizai: Kamigami no Gekirin",
                "The Seven Deadly Sins: Wrath of the Gods"
            ))
            createDummy(context, listOf(
                "Wotaku ni Koi wa Muzukashii",
                "Wotakoi: Love is Hard for Otaku",
                "It's Difficult to Love an Otaku"
            ))
            createDummy(context, listOf(
                "Dungeon ni Deai wo Motomeru no wa Machigatteiru Darou ka Gaiden: Sword Oratoria",
                "Sword Oratoria: Is it Wrong to Try to Pick Up Girls in a Dungeon? On the Side",
                "Danmachi Sword Oratoria"
            ))
            createDummy(context, listOf(
                "Garo: Vanishing Line",
                "GARO -VANISHING LINE-"
            ))
        }
    }

    @Test
    fun animeEpisodeTest1() {
        storage.read().use { context ->
            val matcher = SeriesNameMatcher(context)
            val mediaPart = matcher.matchClosest(
                "Ascendance of a Bookworm (Honzuki no Gekokujou)",
                null,
                1
            )
            Assert.assertEquals(
                "Honzuki no Gekokujou: Shisho ni Naru Tame ni wa Shudan wo Erandeiraremasen",
                mediaPart!!.parent.name
            )
        }
    }

    @Test
    fun animeEpisodeTest2() {
        storage.read().use { context ->
            val matcher = SeriesNameMatcher(context)
            val mediaPart = matcher.matchClosest("Nanatsu no Taizai - Kamigami no Gekirin", null, 1)
            Assert.assertEquals("Nanatsu no Taizai: Kamigami no Gekirin", mediaPart!!.parent.name)
        }
    }

    @Test
    fun animeEpisodeTest3() {
        storage.read().use { context ->
            val matcher = SeriesNameMatcher(context)
            val mediaPart = matcher.matchClosest("Wotaku ni Koi wa Muzukashii (Wotakoi)", null, 1)
            Assert.assertEquals("Wotaku ni Koi wa Muzukashii", mediaPart!!.parent.name)
        }
    }

    @Test
    fun animeEpisodeTest4() {
        storage.read().use { context ->
            val matcher = SeriesNameMatcher(context)
            val mediaPart = matcher.matchClosest("DanMachi - Sword Oratoria", null, 1)
            Assert.assertEquals(
                "Dungeon ni Deai wo Motomeru no wa Machigatteiru Darou ka Gaiden: Sword Oratoria",
                mediaPart!!.parent.name
            )
        }
    }

    @Test
    fun animeEpisodeTest5() {
        storage.read().use { context ->
            val matcher = SeriesNameMatcher(context)
            val mediaPart = matcher.matchClosest("Garo - Vanishing Line", null, 1)
            Assert.assertEquals("Garo: Vanishing Line", mediaPart!!.parent.name)
        }
    }

    private fun createDummy(context: ReadWriteOperation, titles: List<String>) {
        val parent = createSeries(context, titles[0])
        createSeason(context, parent, titles)
    }

    private fun createSeries(context: ReadWriteOperation, seriesTitle: String): MediaUnit {
        val mediaUnit = MediaUnit(
            name = seriesTitle,
            uniqueExtKey = null
        )
        context.save(mediaUnit)
        return mediaUnit
    }

    private fun createSeason(context: ReadWriteOperation, parent: MediaUnit, titles: List<String>) {
        val partCollection = MediaPartCollection(
            parent = parent,
            name = titles[0],
            seasonNumber = null,
            uniqueExtKey = (uniqueGenerator++).toString(),
            sortOrder = null
        )
        for (altTitle in titles) {
            val altTitleEntity = AlternativeTitle(
                parent = partCollection,
                name = altTitle
            )
            context.save(altTitleEntity)
        }
        context.save(partCollection)
        val part = MediaPart(
            name = "Dummy",
            parent = partCollection,
            sortOrder = null,
            uniqueExtKey = (uniqueGenerator++).toString(),
            episodeNumber = 1,
            aired = null
        )
        context.save(part)
    }
}

import base.TestConfiguration
import dynazzviewer.base.ExtDatabase
import dynazzviewer.entities.MediaUnit
import dynazzviewer.services.descriptors.DescriptionPartCollection
import dynazzviewer.storage.sqlite.SqlLiteStorage
import org.junit.Assert
import org.junit.Test

class DescriptionModelTest {
    @Test
    fun createTest() {
        val storage = SqlLiteStorage(TestConfiguration())
        storage.readWrite().use { context ->
            val mediaUnit = MediaUnit("uniq_name", "name")
            val descriptionPartCollection = DescriptionPartCollection(
                    name = "test",
                    alternativeTitles = listOf("test"),
                    uniqueKey = "...",
                    episodes = listOf(),
                    extDatabaseCode = "bfsbs",
                    extDatabase = ExtDatabase.MyAnimeList,
                    sortOrder = null,
                    seasonNumber = null
            )
            context.save(mediaUnit)
            descriptionPartCollection.create(mediaUnit, context)
        }
        storage.read().use { context ->
            val partColl = context.mediaPartCollections().single()
            Assert.assertEquals(1, partColl.alternativeTitles.count())
        }
    }
}

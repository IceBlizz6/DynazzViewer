import base.TestConfiguration
import base.TestUpdateListener
import dynazzviewer.controllers.ServiceDescriptorController
import dynazzviewer.services.descriptors.DescriptionPart
import dynazzviewer.services.descriptors.DescriptionPartCollection
import dynazzviewer.services.descriptors.DescriptionUnit
import dynazzviewer.storage.Storage
import dynazzviewer.storage.sqlite.SqlLiteStorage
import org.junit.Assert
import org.junit.Test

class ServiceDescriptorControllerTest {
    val storage: Storage = SqlLiteStorage(TestConfiguration())
    val controller = ServiceDescriptorController(
        storage = storage,
        listener = TestUpdateListener(),
        descriptorServices = listOf()
    )

    @Test
    fun addInsertTest() {
        val mediaUnitId = controller.add(mock1())
        storage.read().use { context ->
            val mediaUnit = context.mediaUnitById(mediaUnitId)
            Assert.assertEquals("Test Series", mediaUnit.name)
            Assert.assertEquals(1, mediaUnit.children.count())
            Assert.assertEquals(1, context.mediaPartCollections().count())
            Assert.assertEquals(1, mediaUnit.children[0].children.count())
        }
    }

    @Test
    fun addUpdateTest() {
        val mediaUnitId = controller.add(mock1())
        val updatedMediaUnitId = controller.add(mock2())
        Assert.assertEquals(mediaUnitId, updatedMediaUnitId)

        storage.read().use { context ->
            Assert.assertEquals(1, context.mediaUnits().count())
            Assert.assertEquals(1, context.mediaPartCollections().count())
            Assert.assertEquals(2, context.mediaParts().count())
        }
    }

    private fun mock1(): DescriptionUnit {
        val descPart = DescriptionPart(
            name = "Test",
            uniqueKey = "uniq1",
            index = 1,
            aired = null,
            episodeNumber = null
        )
        val descPartCollection = DescriptionPartCollection(
            name = "Test",
            extDatabase = null,
            extDatabaseCode = null,
            episodes = listOf(descPart),
            uniqueKey = "uniq2",
            sortOrder = null,
            seasonNumber = null,
            alternativeTitles = null
        )
        return DescriptionUnit(
            name = "Test Series",
            imageUrls = setOf(),
            children = listOf(descPartCollection),
            uniqueKey = "uniq3",
            tags = setOf("Action", "Comedy")
        )
    }

    private fun mock2(): DescriptionUnit {
        val descPart1 = DescriptionPart(
            name = "Test",
            uniqueKey = "uniq1",
            index = 1,
            aired = null,
            episodeNumber = null
        )
        val descPart2 = DescriptionPart(
            name = "Test",
            uniqueKey = "uniq4",
            index = 2,
            aired = null,
            episodeNumber = null
        )
        val descPartCollection = DescriptionPartCollection(
            name = "Test",
            extDatabase = null,
            extDatabaseCode = null,
            episodes = listOf(descPart1, descPart2),
            uniqueKey = "uniq2",
            sortOrder = null,
            seasonNumber = null,
            alternativeTitles = null
        )
        return DescriptionUnit(
            name = "Test Series",
            imageUrls = setOf(),
            children = listOf(descPartCollection),
            uniqueKey = "uniq3",
            tags = setOf("Action", "Comedy")
        )
    }
}

import base.TestConfiguration
import dynazzviewer.base.ViewStatus
import dynazzviewer.entities.MediaFile
import dynazzviewer.entities.MediaPart
import dynazzviewer.entities.MediaPartCollection
import dynazzviewer.entities.MediaUnit
import dynazzviewer.storage.Storage
import dynazzviewer.storage.sqlite.SqlLiteStorage
import org.junit.Assert
import org.junit.Test

class LinkTest {
    @Test
    fun linkTest() {
        val storage: Storage = SqlLiteStorage(TestConfiguration())
        storage.readWrite().use { context ->
            val mediaUnit = MediaUnit(uniqueExtKey = null, name = "Test", databaseEntry = null)
            val mediaPartCollection = MediaPartCollection(
                parent = mediaUnit,
                name = "Default Name",
                sortOrder = 1,
                uniqueExtKey = "partColl123",
                seasonNumber = null,
                databaseEntry = null
            )
            val mediaPart = MediaPart(
                parent = mediaPartCollection,
                uniqueExtKey = "part2",
                sortOrder = null,
                name = "Test",
                aired = null,
                episodeNumber = null,
                databaseEntry = null
            )
            context.save(mediaUnit)
            context.save(mediaPartCollection)
            context.save(mediaPart)
        }
        storage.readWrite().use { context ->
            val mediaFile = MediaFile("Default Name")
            mediaFile.status = ViewStatus.Viewed
            mediaFile.mediaPart = context.mediaParts().first()
            context.save(mediaFile)
        }
        storage.read().use { context ->
            Assert.assertEquals(ViewStatus.Viewed, context.mediaParts().first().mediaFile!!.status)
        }
    }
}

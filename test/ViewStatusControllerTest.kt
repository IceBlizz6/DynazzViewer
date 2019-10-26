import base.TestConfiguration
import base.TestUpdateListener
import dynazzviewer.base.ViewStatus
import dynazzviewer.controllers.ViewStatusController
import dynazzviewer.entities.MediaFile
import dynazzviewer.entities.MediaPart
import dynazzviewer.entities.MediaPartCollection
import dynazzviewer.entities.MediaUnit
import dynazzviewer.storage.Storage
import dynazzviewer.storage.sqlite.SqlLiteStorage
import org.junit.Assert
import org.junit.Test

class ViewStatusControllerTest() {
    private val storage: Storage = SqlLiteStorage(TestConfiguration())
    private val listener: TestUpdateListener = TestUpdateListener()
    private val controller: ViewStatusController = ViewStatusController(storage, listener)
    var mediaFileId: Int = 0
    var mediaPartId: Int = 0

    init {
        storage.readWrite().use { context ->
            val mediaUnit = MediaUnit(uniqueExtKey = null, name = "Test")
            val mediaPartCollection = MediaPartCollection(
                parent = mediaUnit,
                name = "Default Collection",
                sortOrder = 1,
                uniqueExtKey =
                "partColl"
            )
            val mediaPart = MediaPart(
                parent = mediaPartCollection,
                uniqueExtKey = "part",
                sortOrder = null,
                name = "Test",
                aired = null,
                episodeNumber = null
            )
            context.save(mediaUnit)
            context.save(mediaPartCollection)
            context.save(mediaPart)
            val mediaFile = MediaFile("Default Name")
            mediaFile.mediaPart = mediaPart
            context.save(mediaFile)
            mediaFileId = mediaFile.id
            mediaPartId = mediaPart.id
        }
    }

    @Test
    fun setMediaFileStatusTest() {
        controller.setMediaFileStatus(ViewStatus.Viewed, mediaFileId)

        storage.read().use { context ->
            Assert.assertEquals(ViewStatus.Viewed, context.mediaFiles().first().status)
            Assert.assertEquals(ViewStatus.Viewed, context.mediaFiles().first().mediaPart!!.status)
        }

        Assert.assertEquals(1, listener.mediaFileCounter)
        Assert.assertEquals(1, listener.mediaPartCounter)
    }

    @Test
    fun setMediaPartStatusTest() {
        controller.setMediaPartStatus(ViewStatus.Skipped, mediaPartId)

        storage.read().use { context ->
            Assert.assertEquals(ViewStatus.Skipped, context.mediaFiles().first().status)
            Assert.assertEquals(ViewStatus.Skipped, context.mediaFiles().first().mediaPart!!.status)
        }

        Assert.assertEquals(1, listener.mediaFileCounter)
        Assert.assertEquals(1, listener.mediaPartCounter)
    }

    @Test
    fun resolveViewStatusTest() {
        Assert.assertEquals(ViewStatus.Skipped,
            controller.resolveViewStatus(ViewStatus.None, ViewStatus.Skipped))
        Assert.assertEquals(ViewStatus.Viewed,
            controller.resolveViewStatus(ViewStatus.Skipped, ViewStatus.Viewed))
        Assert.assertEquals(ViewStatus.Viewed,
            controller.resolveViewStatus(ViewStatus.Viewed, ViewStatus.Skipped))
        Assert.assertEquals(ViewStatus.None,
            controller.resolveViewStatus(ViewStatus.None, ViewStatus.None))
    }
}

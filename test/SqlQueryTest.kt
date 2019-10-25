import base.TestConfiguration
import dynazzviewer.entities.MediaFile
import dynazzviewer.entities.MediaUnit
import dynazzviewer.storage.sqlite.SqlLiteStorage
import org.junit.Assert
import org.junit.Test

class SqlQueryTest {
    @Test
    fun mediaFileNameContainsTest() {
        val storage = SqlLiteStorage(TestConfiguration())
        storage.readWrite().use { context ->
            context.save(MediaFile("TestABC"))
            context.save(MediaFile("TestBAC"))
        }
        storage.read().use { context ->
            Assert.assertEquals(2, context.mediaFiles().count())
            Assert.assertEquals("TestBAC", context.mediaFilesByPartialName("tBA").single().name)
            Assert.assertEquals(2, context.mediaFilesByPartialName("Test").count())
        }
    }

    @Test
    fun queryByExtKey() {
        val storage = SqlLiteStorage(TestConfiguration())
        storage.readWrite().use { context ->
            val mediaUnit = MediaUnit(uniqueExtKey = "Key1", name = "Test")
            context.save(mediaUnit)
        }
        storage.read().use { context ->
            val mediaUnit1: MediaUnit? = context.matchExtKey(listOf("Key1"))
            Assert.assertNotNull(mediaUnit1)
            val mediaUnit2: MediaUnit? = context.matchExtKey(listOf("Key2"))
            Assert.assertNull(mediaUnit2)
        }
    }
}

import base.TestConfiguration
import dynazzviewer.base.ViewStatus
import dynazzviewer.entities.MediaFile
import dynazzviewer.entities.MediaPartCollection
import dynazzviewer.entities.MediaUnit
import dynazzviewer.storage.sqlite.SqlLiteStorage
import javax.persistence.PersistenceException
import org.junit.Assert
import org.junit.Test

public class SqlStorageTest {
    @Test
    fun createStorageTest() {
        val storage = SqlLiteStorage(TestConfiguration())
    }

    @Test
    fun autoIdTest() {
        val storage = SqlLiteStorage(TestConfiguration())

        storage.readWrite().use { context ->
            val file = MediaFile("Default Name")
            file.status = ViewStatus.Skipped
            context.save(file)
        }

        storage.read().use { context ->
            Assert.assertEquals(1, context.mediaFiles().count())
            Assert.assertEquals(1, context.mediaFiles().first().id)
            Assert.assertEquals(ViewStatus.Skipped, context.mediaFiles().first().status)
        }
    }

    @Test
    fun fileNameTest() {
        val storage = SqlLiteStorage(TestConfiguration())

        storage.readWrite().use { context ->
            val file = MediaFile("TestABC")
            context.save(file)
        }

        storage.read().use { context ->
            Assert.assertEquals("TestABC", context.mediaFiles().first().name)
        }
    }

    @Test
    fun duplicateExternalUniqueKeyTest() {
        val storage = SqlLiteStorage(TestConfiguration())
        storage.readWrite().use { context ->
            val unit1 = MediaUnit(uniqueExtKey = null, name = "Test")
            unit1.uniqueExtKey = "Key1"
            context.save(unit1)
            val child = MediaPartCollection(
                name = "test",
                parent = unit1,
                sortOrder = 0,
                uniqueExtKey = "partColl",
                seasonNumber = null
            )
            child.uniqueExtKey = "Key1"
            try {
                context.save(child)
                Assert.fail()
            } catch (expected: PersistenceException) {}
        }
    }
}

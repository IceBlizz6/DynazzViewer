import base.TestConfiguration
import dynazzviewer.entities.ExtReference
import dynazzviewer.entities.MediaUnit
import dynazzviewer.storage.sqlite.SqlLiteStorage
import org.junit.Assert
import org.junit.Test

class InheritanceTest {
    @Test
    fun inheritanceTest() {
        val storage = SqlLiteStorage(TestConfiguration())

        storage.readWrite().use { context ->
            val mediaUnit = MediaUnit(
                uniqueExtKey = "test123",
                name = "root",
                databaseEntry = null
            )
            context.save(mediaUnit)
        }

        storage.read().use { context ->
            val refUnit: ExtReference = context.extRefByKey("test123")
            Assert.assertTrue(refUnit is MediaUnit)
        }
    }
}

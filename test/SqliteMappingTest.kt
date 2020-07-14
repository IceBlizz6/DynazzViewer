import base.TestConfiguration
import dynazzviewer.entities.MediaPartCollection
import dynazzviewer.entities.MediaUnit
import dynazzviewer.storage.sqlite.SqlLiteStorage
import org.junit.Assert
import org.junit.Test

class SqliteMappingTest {
    @Test
    fun test() {
        val storage = SqlLiteStorage(TestConfiguration())

        storage.readWrite().use { context ->
            val mediaUnit = MediaUnit(
                name = "Test",
                uniqueExtKey = null,
                databaseEntry = null
            )
            val partCollection = MediaPartCollection(
                parent = mediaUnit,
                name = "Test2",
                uniqueExtKey = "Key",
                sortOrder = null,
                seasonNumber = null,
                databaseEntry = null
            )
            context.save(mediaUnit)
            context.save(partCollection)
        }

        storage.read().use { context ->
            val mediaUnit = context.mediaUnits().single()
            Assert.assertEquals(1, mediaUnit.children.count())
            Assert.assertEquals(mediaUnit, mediaUnit.children[0].parent)
        }
    }
}

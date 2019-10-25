package dynazzviewer.storage.sqlite

import dynazzviewer.entities.ExtReference
import dynazzviewer.entities.MediaFile
import dynazzviewer.entities.MediaPart
import dynazzviewer.entities.MediaPartCollection
import dynazzviewer.entities.MediaUnit
import dynazzviewer.entities.MediaUnitTag
import dynazzviewer.storage.ReadOperation
import dynazzviewer.storage.sqlite.JinqHelper.containsName
import dynazzviewer.storage.sqlite.JinqHelper.fromId
import dynazzviewer.storage.sqlite.JinqHelper.matchAnyExtKey
import dynazzviewer.storage.sqlite.JinqHelper.matchAnyName
import java.io.Closeable
import javax.persistence.EntityManager
import org.jinq.orm.stream.JinqStream

internal open class DataContext(private val storage: SqlLiteStorage) : ReadOperation, Closeable {
    override fun extRefByKey(uniqueKey: String): ExtReference {
        return stream(ExtReference::class.java).where(matchAnyExtKey(listOf(uniqueKey))).onlyValue
    }

    override fun mediaPartCollections(): List<MediaPartCollection> {
        return stream(MediaPartCollection::class.java).toList()
    }

    fun tagsByName(tagNames: List<String>): List<MediaUnitTag> {
        return stream(MediaUnitTag::class.java).where<Exception>(matchAnyName(tagNames)).toList()
    }

    override fun matchExtKey(keys: List<String>): MediaUnit? {
        return stream(ExtReference::class.java)
            .where(matchAnyExtKey(keys))
            .findAny().map { e -> e.root }
            .orElse(null)
    }

    override fun mediaFileById(id: Int): MediaFile {
        return stream(MediaFile::class.java).where(fromId(id)).onlyValue
    }

    override fun mediaFilesByPartialName(partialName: String): List<MediaFile> {
        return stream(MediaFile::class.java).where(containsName(partialName)).toList()
    }

    override fun mediaFiles(): List<MediaFile> {
        return stream(MediaFile::class.java).toList()
    }

    override fun mediaUnitById(id: Int): MediaUnit {
        return stream(MediaUnit::class.java).where(fromId(id)).onlyValue
    }

    override fun mediaPartById(id: Int): MediaPart {
        return stream(MediaPart::class.java).where(fromId(id)).onlyValue
    }

    protected val entityManager: EntityManager = storage.createEntityManager()

    fun <T> stream(entityType: Class<T>): JinqStream<T> {
        return storage.stream(entityManager, entityType)
    }

    override fun mediaUnits(): List<MediaUnit> {
        return stream(MediaUnit::class.java).toList()
    }

    override fun mediaParts(): List<MediaPart> {
        return stream(MediaPart::class.java).toList()
    }

    override fun close() {
        entityManager.close()
    }
}

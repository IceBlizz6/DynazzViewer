package dynazzviewer.storage.sqlite

import dynazzviewer.entities.*
import dynazzviewer.storage.ReadOperation
import dynazzviewer.storage.sqlite.JinqHelper.*
import org.jinq.orm.stream.JinqStream
import java.io.Closeable
import javax.persistence.EntityManager

internal open class DataContext(private val storage: SqlLiteStorage) : ReadOperation, Closeable {
	override fun mediaPartCollections() : List<MediaPartCollection> {
		return stream(MediaPartCollection::class.java).toList()
	}
	
	fun tagsByName(tagNames : List<String>) : List<MediaUnitTag> {
		return stream(MediaUnitTag::class.java).where<Exception>(matchAnyName(tagNames)).toList()
	}
	
	override fun matchExtKey(keys : List<String>): MediaUnit? {
		return stream(ExtReference::class.java).where(matchAnyExtKey(keys)).findAny().map { e -> e.root }.orElse(null)
	}
	
	override fun mediaFileById(id: Int): MediaFile {
		return stream(MediaFile::class.java).where(fromId(id)).onlyValue;
	}
	
	override fun mediaFilesByPartialName(partialName: String): List<MediaFile> {
		return stream(MediaFile::class.java).where(containsName(partialName)).toList()
	}
	
	override fun mediaFiles() : List<MediaFile> {
		return stream(MediaFile::class.java).toList();
	}
	
	override fun mediaUnitById(id: Int): MediaUnit {
		return stream(MediaUnit::class.java).where(fromId(id)).onlyValue;
	}
	
	override fun mediaPartById(id: Int): MediaPart {
		return stream(MediaPart::class.java).where(fromId(id)).onlyValue;
	}
	
	protected val entityManager : EntityManager = storage.createEntityManager()
	
	fun <T> stream(entityType : Class<T>) : JinqStream<T> {
		return storage.stream(entityManager, entityType)
	}
	
	override fun mediaUnits() : List<MediaUnit> {
		return stream(MediaUnit::class.java).toList();
	}
	
	override fun mediaParts() : List<MediaPart> {
		return stream(MediaPart::class.java).toList();
	}
	
	override fun close() {
		entityManager.close()
	}
}
package dynazzviewer.storage.sqlite

import dynazzviewer.entities.EntityModel
import dynazzviewer.entities.MediaUnitTag
import dynazzviewer.storage.ReadWriteOperation
import javax.persistence.EntityTransaction

internal class TransactionDataContext(storage: SqlLiteStorage) : DataContext(storage), ReadWriteOperation {
    private val transaction: EntityTransaction = entityManager.transaction

    init {
        transaction.begin()
    }

    override fun tagsGetOrCreate(tagNames: List<String>): List<MediaUnitTag> {
        val tags = tagsByName(tagNames).toMutableList()
        if (tagNames.count() != tags.size) {
            val existing = tags.map { e -> e.name }
            val missing = tagNames.filter { e -> !existing.contains(e) }
            for (tagName in missing) {
                val tag = MediaUnitTag(
                    name = tagName,
                    mediaUnits = listOf()
                )
                save(tag)
                tags.add(tag)
            }
        }
        return tags
    }

    override fun save(entity: EntityModel) {
        entityManager.persist(entity)
    }

    override fun close() {
        transaction.commit()
        super.close()
    }
}

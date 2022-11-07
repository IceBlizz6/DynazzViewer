package dynazzviewer.storage.sqlite

import dynazzviewer.entities.AnimeSeasonFlag
import dynazzviewer.entities.AnimeSeasonFlagState
import dynazzviewer.entities.EntityModel
import dynazzviewer.entities.ExtDatabase
import dynazzviewer.entities.MediaDatabaseEntry
import dynazzviewer.entities.MediaUnitTag
import dynazzviewer.entities.QAnimeSeasonFlag
import dynazzviewer.entities.QMediaDatabaseEntry
import dynazzviewer.storage.ReadWriteOperation
import jakarta.persistence.EntityTransaction

internal class TransactionDataContext(
    storage: SqlLiteStorage
) : DataContext(storage), ReadWriteOperation {

    private val transaction: EntityTransaction = entityManager.transaction

    init {
        transaction.begin()
    }

    override fun tagsGetOrCreate(tagNames: Set<String>): List<MediaUnitTag> {
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

    override fun mediaEntryGetOrCreate(extDb: ExtDatabase, extDbCode: String): MediaDatabaseEntry {
        val match = stream(QMediaDatabaseEntry.mediaDatabaseEntry)
            .filter { it.mediaDatabase.eq(extDb).and(it.code.eq(extDbCode)) }
            .fetchSingleOrNull()

        if (match == null) {
            val newEntry = MediaDatabaseEntry(
                mediaDatabase = extDb,
                code = extDbCode
            )
            save(newEntry)
            return newEntry
        } else {
            return match
        }
    }

    override fun setAnimeSeasonFlag(malId: Int, flag: AnimeSeasonFlagState) {
        val entity = stream(QAnimeSeasonFlag.animeSeasonFlag)
            .filter { it.malId.eq(malId) }
            .fetchSingleOrNull()

        if (entity == null) {
            val flagEntity = AnimeSeasonFlag(
                malId = malId,
                flag = flag
            )
            save(flagEntity)
        } else {
            entity.flag = flag
            save(entity)
        }
    }

    override fun save(entity: EntityModel) {
        entityManager.persist(entity)
    }

    override fun delete(entity: EntityModel) {
        entityManager.remove(entity)
    }

    override fun close() {
        transaction.commit()
        super.close()
    }
}

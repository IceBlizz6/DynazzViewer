package dynazzviewer.storage.sqlite

import com.querydsl.core.types.dsl.EntityPathBase
import dynazzviewer.entities.*
import dynazzviewer.entities.AnimeSeasonFlagState
import dynazzviewer.entities.ExtDatabase
import dynazzviewer.storage.MediaIdentity
import dynazzviewer.storage.ReadOperation
import dynazzviewer.storage.query.QueryStream
import java.io.Closeable
import javax.persistence.EntityManager

internal open class DataContext(
    private val storage: SqlLiteStorage
) : ReadOperation, Closeable {
    protected val entityManager: EntityManager = storage.createEntityManager()

    override fun alternativeTitleLike(sqlLikeString: String): List<AlternativeTitle> {
        return stream(QAlternativeTitle.alternativeTitle)
            .filter { it.name.like(sqlLikeString) }
            .fetchList()
    }

    override fun mediaFilesByName(names: Set<String>): Map<String, MediaFile> {
        return stream(QMediaFile.mediaFile)
            .filter { it.name.`in`(names) }
            .fetchList()
            .map { it.name to it }
            .toMap()
    }

    override fun mediaUnitsLike(sqlLikeString: String): List<MediaUnit> {
        return stream(QMediaUnit.mediaUnit)
            .filter { it.name.like(sqlLikeString) }
            .fetchList()
    }

    override fun mediaPartCollectionsLike(sqlLikeString: String): List<MediaPartCollection> {
        return stream(QMediaPartCollection.mediaPartCollection)
            .filter { it.name.like(sqlLikeString) }
            .fetchList()
    }

    override fun mediaPartCollectionByCode(db: ExtDatabase, code: String): MediaPartCollection? {
        return stream(QMediaPartCollection.mediaPartCollection)
            .filter { it._super.databaseEntry.mediaDatabase.eq(db) }
            .filter { it._super.databaseEntry.code.eq(code) }
            .fetchSingleOrNull()
    }

    override fun mediaUnitExist(list: List<MediaIdentity>): Map<MediaIdentity, Boolean> {
        val groups = list.groupBy { it.extDb }
        val allMatches = mutableListOf<MediaIdentity>()
        for (group in groups) {
            val codes: List<String> = group.value.map { it.extDbCode }
            val matches: List<String> = stream(QMediaDatabaseEntry.mediaDatabaseEntry)
                .filter { it.mediaDatabase.eq(group.key) }
                .filter { it.code.`in`(codes) }
                .fetchList { it.code }
            allMatches.addAll(group.value.filter { matches.contains(it.extDbCode) })
        }

        return list.map { identity ->
            identity to allMatches.contains(identity)
        }.toMap()
    }

    override fun animeSeasonSeries(malIds: List<Int>): Map<Int, AnimeSeasonFlagState> {
        return stream(QAnimeSeasonFlag.animeSeasonFlag)
            .filter { it.malId.`in`(malIds) }
            .fetchList()
            .map { it.malId to it.flag }
            .toMap()
    }

    override fun extRefByKey(uniqueKey: String): ExtReference {
        return stream(QExtReference.extReference)
            .filter { it.uniqueExtKey.`in`(uniqueKey) }
            .fetchSingle()
    }

    override fun mediaPartCollections(): List<MediaPartCollection> {
        return stream(QMediaPartCollection.mediaPartCollection)
            .fetchList()
    }

    fun tagsByName(tagNames: Collection<String>): List<MediaUnitTag> {
        return stream(QMediaUnitTag.mediaUnitTag)
            .filter { it.name.`in`(tagNames) }
            .fetchList()
    }

    override fun matchExtKey(keys: List<String>): MediaUnit? {
        return stream(QExtReference.extReference)
            .filter { it.uniqueExtKey.`in`(keys) }
            .fetchList()
            .map { it.root }
            .distinctBy { it.id }
            .singleOrNull()
    }

    override fun mediaFileById(id: Int): MediaFile {
        return stream(QMediaFile.mediaFile)
            .filter { it.id.eq(id) }
            .fetchSingle()
    }

    override fun mediaFilesByPartialName(partialName: String): List<MediaFile> {
        return stream(QMediaFile.mediaFile)
            .filter { it.name.containsIgnoreCase(partialName) }
            .fetchList()
    }

    override fun mediaFiles(): List<MediaFile> {
        return stream(QMediaFile.mediaFile).fetchList()
    }

    override fun mediaUnitById(id: Int): MediaUnit {
        return stream(QMediaUnit.mediaUnit)
            .filter { it.id.eq(id) }
            .fetchSingle()
    }

    override fun mediaPartById(id: Int): MediaPart {
        return stream(QMediaPart.mediaPart).filter { it.id.eq(id) }.fetchSingle()
    }

    fun <QT : EntityPathBase<T>, T> stream(source: QT): QueryStream<QT, T> {
        return storage.stream(entityManager, source)
    }

    override fun mediaUnits(): List<MediaUnit> {
        return stream(QMediaUnit.mediaUnit).fetchList()
    }

    override fun mediaParts(): List<MediaPart> {
        return stream(QMediaPart.mediaPart).fetchList()
    }

    override fun close() {
        entityManager.close()
    }
}

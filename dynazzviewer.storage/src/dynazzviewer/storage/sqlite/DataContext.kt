package dynazzviewer.storage.sqlite

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.EntityPathBase
import dynazzviewer.entities.AlternativeTitle
import dynazzviewer.entities.AnimeSeasonFlagState
import dynazzviewer.entities.ExtDatabase
import dynazzviewer.entities.MediaFile
import dynazzviewer.entities.MediaPart
import dynazzviewer.entities.MediaPartCollection
import dynazzviewer.entities.MediaUnit
import dynazzviewer.entities.MediaUnitTag
import dynazzviewer.entities.QAlternativeTitle
import dynazzviewer.entities.QAnimeSeasonFlag
import dynazzviewer.entities.QMediaDatabaseEntry
import dynazzviewer.entities.QMediaFile
import dynazzviewer.entities.QMediaPart
import dynazzviewer.entities.QMediaPartCollection
import dynazzviewer.entities.QMediaUnit
import dynazzviewer.entities.QMediaUnitTag
import dynazzviewer.storage.MediaIdentity
import dynazzviewer.storage.MediaUnitSort
import dynazzviewer.storage.ReadOperation
import dynazzviewer.storage.SortOrder
import dynazzviewer.storage.query.QueryBuilder
import dynazzviewer.storage.query.QueryStream
import java.io.Closeable
import javax.persistence.EntityManager

internal open class DataContext(
    private val storage: SqlLiteStorage
) : ReadOperation, Closeable {
    companion object {
        const val inPredicateChunkSize = 500
    }

    protected val entityManager: EntityManager = storage.createEntityManager()

    override fun alternativeTitleLike(sqlLikeString: String): List<AlternativeTitle> {
        return stream(QAlternativeTitle.alternativeTitle)
            .filter { it.name.like(sqlLikeString) }
            .fetchList()
    }

    override fun mediaFilesByName(names: Set<String>): Map<String, MediaFile> {
        return names
            .chunked(inPredicateChunkSize)
            .map { nameListChunk ->
                stream(QMediaFile.mediaFile)
                    .filter { it.name.`in`(nameListChunk) }
                    .fetchList()
                    .associateBy { it.name }
            }
            .flatMap { it.entries }
            .associate { it.key to it.value }
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
            .filter { it.databaseEntry.mediaDatabase.eq(db) }
            .filter { it.databaseEntry.code.eq(code) }
            .fetchSingleOrNull()
    }

    override fun mediaUnitByCode(db: ExtDatabase, code: String): MediaUnit? {
        return stream(QMediaUnit.mediaUnit)
            .filter { it.databaseEntry.mediaDatabase.eq(db) }
            .filter { it.databaseEntry.code.eq(code) }
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

    override fun mediaPartCollections(): List<MediaPartCollection> {
        return stream(QMediaPartCollection.mediaPartCollection)
            .fetchList()
    }

    fun tagsByName(tagNames: Collection<String>): List<MediaUnitTag> {
        return stream(QMediaUnitTag.mediaUnitTag)
            .filter { it.name.`in`(tagNames) }
            .fetchList()
    }

    override fun mediaUnitByKey(uniqueKey: String): MediaUnit? {
        return stream(QMediaUnit.mediaUnit)
            .filter { it.uniqueKey.eq(uniqueKey) }
            .fetchSingleOrNull()
    }

    override fun mediaPartCollectionByKey(uniqueKey: String): MediaPartCollection? {
        return stream(QMediaPartCollection.mediaPartCollection)
            .filter { it.uniqueKey.eq(uniqueKey) }
            .fetchSingleOrNull()
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

    override fun mediaUnits(
        skip: Int,
        take: Int,
        sort: MediaUnitSort,
        order: SortOrder
    ): List<MediaUnit> {
        return stream(QMediaUnit.mediaUnit)
            .let { stream ->
                when (sort) {
                    MediaUnitSort.LAST_EPISODE_AIRED -> stream.orderByWithBuilder {
                            builder: QueryBuilder<QMediaUnit, MediaUnit, OrderSpecifier<*>> ->
                        builder
                            .flatMap { it.children }
                            .flatMap { it.children }
                            .build { order.func(it.aired) }
                    }
                    MediaUnitSort.ID -> stream.orderBy { order.func(it.id) }
                    MediaUnitSort.NAME -> stream.orderBy { order.func(it.name) }
                }
            }
            .skip(skip.toLong())
            .limit(take.toLong())
            .fetchList()
    }

    override fun mediaParts(): List<MediaPart> {
        return stream(QMediaPart.mediaPart).fetchList()
    }

    override fun close() {
        entityManager.close()
    }
}

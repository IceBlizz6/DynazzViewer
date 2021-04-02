package dynazzviewer.storage

import dynazzviewer.base.AnimeSeasonFlagState
import dynazzviewer.base.ExtDatabase
import dynazzviewer.base.ViewStatus
import dynazzviewer.entities.*
import java.io.Closeable

interface ReadOperation : Closeable {
    fun mediaUnitById(id: Int): MediaUnit

    fun mediaPartById(id: Int): MediaPart

    fun mediaUnits(): List<MediaUnit>

    fun mediaPartCollections(): List<MediaPartCollection>

    fun mediaParts(): List<MediaPart>

    fun matchExtKey(keys: List<String>): MediaUnit?

    fun extRefByKey(uniqueKey: String): ExtReference

    fun mediaFileById(id: Int): MediaFile

    fun mediaFiles(): List<MediaFile>

    fun mediaFilesByName(names: Set<String>): Map<String, Pair<Int, ViewStatus>>

    fun mediaFilesByPartialName(partialName: String): List<MediaFile>

    fun alternativeTitleLike(sqlLikeString: String): List<AlternativeTitle>

    fun mediaUnitsLike(sqlLikeString: String): List<MediaUnit>

    fun mediaPartCollectionsLike(sqlLikeString: String): List<MediaPartCollection>

    fun mediaUnitExist(list: List<MediaIdentity>): Map<MediaIdentity, Boolean>

    fun animeSeasonSeries(malIds: List<Int>): Map<Int, AnimeSeasonFlagState>

    fun mediaPartCollectionByCode(db: ExtDatabase, code: String): MediaPartCollection?
}

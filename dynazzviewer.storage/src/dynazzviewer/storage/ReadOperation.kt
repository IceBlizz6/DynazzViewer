package dynazzviewer.storage

import dynazzviewer.entities.AlternativeTitle
import dynazzviewer.entities.AnimeSeasonFlagState
import dynazzviewer.entities.ExtDatabase
import dynazzviewer.entities.MediaFile
import dynazzviewer.entities.MediaPart
import dynazzviewer.entities.MediaPartCollection
import dynazzviewer.entities.MediaUnit
import java.io.Closeable

interface ReadOperation : Closeable {
    fun mediaUnitById(id: Int): MediaUnit

    fun mediaPartById(id: Int): MediaPart

    fun mediaUnits(): List<MediaUnit>

    fun mediaUnits(skip: Int, take: Int, sort: MediaUnitSort, order: SortOrder): List<MediaUnit>

    fun mediaPartCollections(): List<MediaPartCollection>

    fun mediaParts(): List<MediaPart>

    fun mediaUnitByKey(uniqueKey: String): MediaUnit?

    fun mediaPartCollectionByKey(uniqueKey: String): MediaPartCollection?

    fun mediaFileById(id: Int): MediaFile

    fun mediaFiles(): List<MediaFile>

    fun mediaFilesByName(names: Set<String>): Map<String, MediaFile>

    fun mediaFilesByPartialName(partialName: String): List<MediaFile>

    fun alternativeTitleLike(sqlLikeString: String): List<AlternativeTitle>

    fun mediaUnitsLike(sqlLikeString: String): List<MediaUnit>

    fun mediaPartCollectionsLike(sqlLikeString: String): List<MediaPartCollection>

    fun mediaUnitExist(list: List<MediaIdentity>): Map<MediaIdentity, Boolean>

    fun animeSeasonSeries(malIds: List<Int>): Map<Int, AnimeSeasonFlagState>

    fun mediaPartCollectionByCode(db: ExtDatabase, code: String): MediaPartCollection?

    fun mediaUnitByCode(db: ExtDatabase, code: String): MediaUnit?
}

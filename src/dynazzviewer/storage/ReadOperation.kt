package dynazzviewer.storage

import dynazzviewer.entities.AlternativeTitle
import dynazzviewer.entities.ExtReference
import dynazzviewer.entities.MediaFile
import dynazzviewer.entities.MediaPart
import dynazzviewer.entities.MediaPartCollection
import dynazzviewer.entities.MediaUnit
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

    fun mediaFilesByPartialName(partialName: String): List<MediaFile>

    fun alternativeTitleLike(sqlLikeString: String): List<AlternativeTitle>

    fun mediaUnitsLike(sqlLikeString: String): List<MediaUnit>
}

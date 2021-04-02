package dynazzviewer.entities

import dynazzviewer.base.UniqueKey
import dynazzviewer.base.ViewStatus
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.OneToOne

@Entity
class MediaPart(
    @ManyToOne
    var parent: MediaPartCollection,
    uniqueExtKey: String,
    databaseEntry: MediaDatabaseEntry?,
    var name: String,
    var sortOrder: Int?,
    var aired: LocalDate?,
    var episodeNumber: Int?
) : EntityModel, IdContainer, ExtReference(uniqueExtKey, databaseEntry), UniqueKey {
    var status: ViewStatus = ViewStatus.None

    @OneToOne(mappedBy = "mediaPart")
    var mediaFile: MediaFile? = null
        private set

    override val uniqueKey: String
        get() = uniqueExtKey!!

    override val root: MediaUnit
        get() = parent.parent
}

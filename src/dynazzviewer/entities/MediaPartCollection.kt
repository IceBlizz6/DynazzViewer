package dynazzviewer.entities

import dynazzviewer.base.UniqueKey
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
class MediaPartCollection(
    @ManyToOne
       val parent: MediaUnit,
    uniqueExtKey: String,
    databaseEntry: MediaDatabaseEntry?,
    var name: String,
    var sortOrder: Int?,
    var seasonNumber: Int?
) : EntityModel, ExtReference(uniqueExtKey, databaseEntry), UniqueKey {
    override val uniqueKey: String
        get() = uniqueExtKey!!

    @OneToMany(mappedBy = "parent")
    lateinit var children: List<MediaPart>
        private set

    @OneToMany(mappedBy = "parent")
    lateinit var alternativeTitles: List<AlternativeTitle>
        private set

    override val root: MediaUnit
        get() = parent
}

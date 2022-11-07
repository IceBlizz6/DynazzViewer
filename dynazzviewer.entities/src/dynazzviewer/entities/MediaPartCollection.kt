package dynazzviewer.entities

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
class MediaPartCollection(
    @ManyToOne
    val parent: MediaUnit,
    override val uniqueKey: String,
    @ManyToOne
    val databaseEntry: MediaDatabaseEntry?,
    var name: String,
    var sortOrder: Int?,
    var seasonNumber: Int?
) : EntityModel, UniqueKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0
        private set

    @OneToMany(mappedBy = "parent")
    lateinit var children: List<MediaPart>
        private set

    @OneToMany(mappedBy = "parent")
    lateinit var alternativeTitles: List<AlternativeTitle>
        private set
}

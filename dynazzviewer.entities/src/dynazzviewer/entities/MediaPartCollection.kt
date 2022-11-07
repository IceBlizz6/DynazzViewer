package dynazzviewer.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

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

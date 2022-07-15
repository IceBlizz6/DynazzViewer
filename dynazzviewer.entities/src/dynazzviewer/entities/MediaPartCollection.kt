package dynazzviewer.entities

import javax.persistence.*

@Entity
class MediaPartCollection(
    @ManyToOne
    val parent: MediaUnit,
    override val uniqueKey: String,
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

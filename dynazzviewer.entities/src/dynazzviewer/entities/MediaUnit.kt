package dynazzviewer.entities

import javax.persistence.*

@Entity
class MediaUnit(
    val uniqueKey: String?,
    val databaseEntry: MediaDatabaseEntry?,
    var name: String
) : EntityModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0
        private set

    @ManyToMany
    var tags: MutableList<MediaUnitTag> = mutableListOf()
        private set

    @OneToMany(mappedBy = "parent")
    lateinit var children: List<MediaPartCollection>
        private set

    @OneToMany(mappedBy = "mediaUnit")
    lateinit var images: List<MediaImage>
        private set
}

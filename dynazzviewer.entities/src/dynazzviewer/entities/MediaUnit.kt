package dynazzviewer.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

@Entity
class MediaUnit(
    val uniqueKey: String?,
    @ManyToOne
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

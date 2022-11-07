package dynazzviewer.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany

@Entity
class MediaUnitTag(
    @Column(unique = true)
    val name: String,
    @ManyToMany(mappedBy = "tags")
    val mediaUnits: List<MediaUnit>
) : EntityModel, UniqueKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0
        private set

    override val uniqueKey: String
        get() = name
}

package dynazzviewer.entities

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToMany

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

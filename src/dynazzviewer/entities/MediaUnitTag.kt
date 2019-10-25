package dynazzviewer.entities

import dynazzviewer.base.UniqueKey
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToMany

@Entity
class MediaUnitTag(
    @Column(unique = true)
       override val name: String,
    @ManyToMany(mappedBy = "tags")
       val mediaUnits: List<MediaUnit>
) : EntityModel, NameContainer, UniqueKey {
    override val uniqueKey: String
        get() = name

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0
        private set
}

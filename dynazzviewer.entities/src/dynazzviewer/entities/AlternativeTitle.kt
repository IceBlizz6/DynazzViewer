package dynazzviewer.entities

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
class AlternativeTitle(
    @ManyToOne
    val parent: MediaPartCollection,
    override val name: String
) : UniqueKey, NameContainer, EntityModel {
    override val uniqueKey: String
        get() = name

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0
        private set
}

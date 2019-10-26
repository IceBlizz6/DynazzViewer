package dynazzviewer.entities

import dynazzviewer.base.UniqueKey
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
class AlternativeTitle(
    @ManyToOne
    val parent: MediaPartCollection,
    val title: String
) : UniqueKey, EntityModel {
    override val uniqueKey: String
        get() = title

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0
        private set
}

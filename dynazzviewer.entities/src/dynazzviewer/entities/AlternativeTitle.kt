package dynazzviewer.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class AlternativeTitle(
    @ManyToOne
    val parent: MediaPartCollection,
    val name: String
) : EntityModel, UniqueKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0
        private set

    override val uniqueKey: String
        get() = name
}

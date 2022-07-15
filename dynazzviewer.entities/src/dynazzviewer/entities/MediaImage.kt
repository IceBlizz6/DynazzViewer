package dynazzviewer.entities

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
class MediaImage(
    @ManyToOne
    val mediaUnit: MediaUnit,
    val url: String
) : EntityModel, UniqueKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0
        private set

    override val uniqueKey: String
        get() = url
}

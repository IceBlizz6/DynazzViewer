package dynazzviewer.entities

import dynazzviewer.base.UniqueKey
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
    override val uniqueKey: String
        get() = url

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0
        private set
}

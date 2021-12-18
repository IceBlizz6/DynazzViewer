package dynazzviewer.entities

import dynazzviewer.base.ViewStatus
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToOne

@Entity
data class MediaFile(
    @Column(unique = true)
    override var name: String,
    var status: ViewStatus = ViewStatus.None,
    @OneToOne
    var mediaPart: MediaPart? = null
) : EntityModel, IdContainer, NameContainer, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Int = 0
        private set
}

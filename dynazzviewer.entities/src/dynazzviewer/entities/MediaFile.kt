package dynazzviewer.entities

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
    var name: String,
    var status: ViewStatus = ViewStatus.None,
    @OneToOne
    var mediaPart: MediaPart? = null
) : EntityModel, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0
        private set
}

package dynazzviewer.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import java.io.Serializable

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

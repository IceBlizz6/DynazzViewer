package dynazzviewer.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import java.time.LocalDate

@Entity
class MediaPart(
    @ManyToOne
    var parent: MediaPartCollection,
    override val uniqueKey: String,
    var name: String,
    var sortOrder: Int?,
    var aired: LocalDate?,
    var episodeNumber: Int?
) : EntityModel, UniqueKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0
        private set

    var status: ViewStatus = ViewStatus.None

    @OneToOne(mappedBy = "mediaPart")
    var mediaFile: MediaFile? = null
        private set
}

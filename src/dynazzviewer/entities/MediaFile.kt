package dynazzviewer.entities

import dynazzviewer.base.ViewStatus
import javax.persistence.*;
import java.io.*

@Entity
data class MediaFile (
	override var name : String,
	var status : ViewStatus = ViewStatus.None,
	@OneToOne
	var mediaPart : MediaPart? = null
) : EntityModel, IdContainer, NameContainer, Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	override var id : Int = 0
		private set
}

package dynazzviewer.entities

import dynazzviewer.base.UniqueKey
import dynazzviewer.base.ViewStatus
import java.time.LocalDate
import javax.persistence.*

@Entity
class MediaPart(
	@ManyToOne
	var parent : MediaPartCollection,
	uniqueExtKey : String,
	var name : String,
	var sortOrder : Int?,
	var aired : LocalDate?
) : EntityModel, IdContainer, ExtReference(uniqueExtKey), UniqueKey {
	var status : ViewStatus = ViewStatus.None
	
	@OneToOne(mappedBy = "mediaPart")
	var mediaFile : MediaFile? = null
		private set
	
	override val uniqueKey: String
		get() = uniqueExtKey!!
	
	override val root: MediaUnit
		get() = parent.parent
}

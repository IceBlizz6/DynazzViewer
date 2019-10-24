package dynazzviewer.entities

import dynazzviewer.base.UniqueKey
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
	
	override val uniqueKey: String
		get() = uniqueExtKey!!
	
	override val root: MediaUnit
		get() = parent.parent
}

package dynazzviewer.entities

import dynazzviewer.base.UniqueKey
import javax.persistence.*

@Entity
class MediaPartCollection(
	@ManyToOne
	val parent : MediaUnit,
	uniqueExtKey: String,
	var name : String,
	var sortOrder : Int?
) : EntityModel, ExtReference(uniqueExtKey), UniqueKey {
	override val uniqueKey: String
		get() = uniqueExtKey!!
	
	@OneToMany
	var children : MutableList<MediaPart> = mutableListOf()
		private set
	
	override val root: MediaUnit
		get() = parent
}

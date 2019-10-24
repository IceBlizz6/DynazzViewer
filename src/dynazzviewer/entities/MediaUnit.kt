package dynazzviewer.entities

import javax.persistence.*

@Entity
class MediaUnit(
	uniqueExtKey: String?,
	var name : String
) : EntityModel, ExtReference(uniqueExtKey), IdContainer {
	@ManyToMany
	var tags : MutableList<MediaUnitTag> = mutableListOf()
		private set
	
	@OneToMany
	var children : MutableList<MediaPartCollection> = mutableListOf()
		private set
	
	@OneToMany
	var images : MutableList<MediaImage> = mutableListOf()
		private set
	
	override val root: MediaUnit
		get() = this
}

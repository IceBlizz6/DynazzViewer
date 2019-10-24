package dynazzviewer.entities

import dynazzviewer.base.UniqueKey
import javax.persistence.*

@Entity
class MediaImage(
	@ManyToOne
	var mediaUnit : MediaUnit,
	val url : String
) : EntityModel, UniqueKey {
	override val uniqueKey: String
		get() = url
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id : Int = 0
		private set
}

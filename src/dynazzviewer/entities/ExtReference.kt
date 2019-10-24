package dynazzviewer.entities

import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class ExtReference (
	/**
	 * Unique value for external references
	 * Matched to update existing media from external sources
	 */
	@Column(unique = true)
	var uniqueExtKey : String?
) {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id : Int = 0
		private set
	
	abstract val root : MediaUnit
}

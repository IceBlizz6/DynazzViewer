package dynazzviewer.entities

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Inheritance
import javax.persistence.InheritanceType

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class ExtReference(
    /**
	    * Unique value for external references
	    * Matched to update existing media from external sources
	    */
       @Column(unique = true)
       var uniqueExtKey: String?
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0
        private set

    abstract val root: MediaUnit
}

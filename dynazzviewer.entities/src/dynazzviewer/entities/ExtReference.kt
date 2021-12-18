package dynazzviewer.entities

import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class ExtReference(
    /**
     * Unique value for external references
     * Matched to update existing media from external sources
     */
    @Column(unique = true)
    open var uniqueExtKey: String?,
    @ManyToOne
    open var databaseEntry: MediaDatabaseEntry?
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Int = 0
        protected set

    abstract val root: MediaUnit
}

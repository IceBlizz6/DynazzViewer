package dynazzviewer.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class MediaDatabaseEntry(
    @Id val mediaDatabase: ExtDatabase,
    @Id val code: String
) : EntityModel, java.io.Serializable

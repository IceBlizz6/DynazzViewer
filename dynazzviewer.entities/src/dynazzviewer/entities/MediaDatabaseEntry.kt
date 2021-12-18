package dynazzviewer.entities

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class MediaDatabaseEntry(
    @Id val mediaDatabase: ExtDatabase,
    @Id val code: String
) : EntityModel, java.io.Serializable

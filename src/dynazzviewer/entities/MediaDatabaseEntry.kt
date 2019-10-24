package dynazzviewer.entities

import dynazzviewer.base.ExtDatabase
import javax.persistence.Id

class MediaDatabaseEntry(
	@Id
	var mediaDatabase : ExtDatabase,
	@Id
	var mediaRef : ExtReference,
	var code : String
) : EntityModel {}

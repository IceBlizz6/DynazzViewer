package dynazzviewer.services.descriptors

import dynazzviewer.entities.ExtDatabase
import dynazzviewer.storage.MediaIdentity

class ResultHeader(
    val name: String,
    val imageUrl: String,
    override val extDb: ExtDatabase,
    override val extDbCode: String
) : MediaIdentity {
    val extReference: Pair<ExtDatabase, String>
        get() = Pair(extDb, extDbCode)
}

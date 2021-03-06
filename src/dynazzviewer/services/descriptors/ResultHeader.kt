package dynazzviewer.services.descriptors

import dynazzviewer.base.ExtDatabase

class ResultHeader(
    val name: String,
    val imageUrl: String,
    val extDb: ExtDatabase,
    val extDbCode: String
) {
    val extReference: Pair<ExtDatabase, String>
        get() = Pair(extDb, extDbCode)
}

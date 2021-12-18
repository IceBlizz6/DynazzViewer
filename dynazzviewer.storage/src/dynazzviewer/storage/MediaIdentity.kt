package dynazzviewer.storage

import dynazzviewer.entities.ExtDatabase

interface MediaIdentity {
    val extDb: ExtDatabase

    val extDbCode: String
}

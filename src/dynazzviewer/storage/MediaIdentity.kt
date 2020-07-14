package dynazzviewer.storage

import dynazzviewer.base.ExtDatabase

interface MediaIdentity {
    val extDb: ExtDatabase

    val extDbCode: String
}

package dynazzviewer.base

import dynazzviewer.storage.StorageMode

interface Configuration {
    val rootStorageDirectory: String

    val storageMode: StorageMode
}

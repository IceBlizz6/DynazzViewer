package dynazzviewer.config

import dynazzviewer.storage.StorageMode

interface Configuration {
    val rootStorageDirectory: String

    val storageMode: StorageMode
}

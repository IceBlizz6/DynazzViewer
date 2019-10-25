package base

import dynazzviewer.base.Configuration
import dynazzviewer.storage.StorageMode

class TestConfiguration : Configuration {
    override val rootStorageDirectory: String
        get() = throw NotImplementedError()

    override val storageMode: StorageMode
        get() = StorageMode.MEMORY
}

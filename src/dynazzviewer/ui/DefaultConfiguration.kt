package dynazzviewer.ui

import dynazzviewer.base.Configuration
import dynazzviewer.filesystem.FileConfiguration
import dynazzviewer.storage.StorageMode
import java.io.File
import tornadofx.ConfigProperties

class DefaultConfiguration(
    private val config: ConfigProperties
) : Configuration, FileConfiguration {
    companion object {
        const val cacheDirectory = "cache"
        const val userDirectory = "user"
        const val configPropertiesFileName = "config.properties"
        private const val listSeparator = "|"
    }

    override var videoExtensions: Set<String>
        get() = readSet("VideoExtensions", setOf("mkv", "avi"))
        set(value) = writeSet("VideoExtensions", value)

    override var extensionFilter: Set<String>
        get() = readSet("FileExtensions", setOf("mkv", "avi"))
        set(value) = writeSet("FileExtensions", value)

    override val cacheDirectoryPath: String
        get() = cacheDirectory

    override val rootStorageDirectory: String
        get() = userDirectory

    override val storageMode: StorageMode = StorageMode.FILE

    override var rootDirectoryPaths: Set<String>
        get() = readSet("directories", setOf())
        set(value) = writeSet("directories", value)

    init {
        ensureDirectoryExists(cacheDirectoryPath)
        ensureDirectoryExists(rootStorageDirectory)
    }

    private fun ensureDirectoryExists(path: String) {
        val directory = File(path)
        if (!directory.exists()) {
            directory.mkdirs()
        }
    }

    private fun readSet(key: String, defaultValue: Set<String>): Set<String> {
        val storedValue = config.string(key)
        if (storedValue == null || storedValue.isEmpty()) {
            return defaultValue
        } else {
            return storedValue
                .splitToSequence(listSeparator)
                .toSet()
        }
    }

    private fun writeSet(key: String, value: Set<String>) {
        val valueString = value.joinToString(listSeparator)
        config[key] = valueString
        config.save()
    }
}

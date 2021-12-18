package dynazzviewer.ui.config

import dynazzviewer.config.Configuration
import dynazzviewer.files.FileConfiguration
import dynazzviewer.storage.StorageMode
import java.io.File

class DefaultConfiguration(
    private val config: SettingController
) : Configuration, FileConfiguration, UserConfiguration {
    companion object {
        const val cacheDirectory = "cache"
        const val userDirectory = "user"
        const val configPropertiesFileName = "config.properties"
        const val clientOrigin: String = "http://localhost:8080"
        private const val listSeparator = "|"
    }

    override var mediaPlayerApplicationPath: String?
        get() = config.string("mediaPlayerApplicationPath")
        set(value) {
            assignNullIfEmpty("mediaPlayerApplicationPath", value)
            config.save()
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

    private fun assignNullIfEmpty(key: String, value: String?) {
        val assignableValue = nullIfEmpty(value)
        if (assignableValue == null) {
            config.remove(key)
        } else {
            config.set("mediaPlayerApplicationPath", assignableValue)
        }
    }

    private fun nullIfEmpty(value: String?): String? {
        if (value != null && value.isEmpty()) {
            return null
        } else {
            return value
        }
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
        config.set(key, valueString)
        config.save()
    }
}

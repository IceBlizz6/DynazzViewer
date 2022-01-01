package dynazzviewer.ui.web

import dynazzviewer.ui.config.SettingController
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.Properties

class WebSettingsController : SettingController {
    private val propertiesPath = "user/config.properties"
    private val properties = Properties()

    init {
        try {
            val fileInputStream = FileInputStream(propertiesPath)
            properties.load(fileInputStream)
        } catch (_: FileNotFoundException) { }
    }

    override fun save() {
        val fileOutputStream = FileOutputStream(propertiesPath)
        properties.store(fileOutputStream, "")
    }

    override fun string(key: String): String? {
        return properties[key]?.toString()
    }

    override fun remove(key: String) {
        properties.remove(key)
    }

    override fun set(key: String, value: String) {
        properties[key] = value
    }
}

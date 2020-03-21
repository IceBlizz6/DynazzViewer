package dynazzviewer.ui.config

interface SettingController {
    fun save()

    fun string(key: String): String?

    fun remove(key: String)

    fun set(key: String, value: String)
}

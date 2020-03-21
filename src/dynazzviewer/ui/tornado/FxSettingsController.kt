package dynazzviewer.ui.tornado

import dynazzviewer.ui.config.SettingController
import tornadofx.ConfigProperties

class FxSettingsController(
    private val config: ConfigProperties
) : SettingController {
    override fun save() {
        config.save()
    }

    override fun string(key: String): String? {
        return config.string(key)
    }

    override fun remove(key: String) {
        config.remove(key)
    }

    override fun set(key: String, value: String) {
        config.set(key, value)
    }
}

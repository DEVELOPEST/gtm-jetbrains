package ee.taltech.gtm.service

import com.intellij.openapi.components.*

@State(
        name = "GtmEnhanced",
        storages = [Storage("gtm.xml")] // Storage(value = StoragePathMacros.WORKSPACE_FILE)
)
class ConfigService : PersistentStateComponent<ConfigService.Config> {
    data class Config(
            var isGtmDisabled: Boolean? = null
    )

    private var config = Config()

    override fun getState(): Config {
        return config
    }

    override fun loadState(state: Config) {
        config = state
    }

    fun setGtmDisabled(disabled: Boolean) {
        config.isGtmDisabled = disabled
    }
}
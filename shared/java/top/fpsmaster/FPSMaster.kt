package top.fpsmaster

import top.fpsmaster.features.GlobalSubmitter
import top.fpsmaster.features.command.CommandManager
import top.fpsmaster.features.manager.ModuleManager
import top.fpsmaster.font.FontManager
import top.fpsmaster.modules.account.AccountManager
import top.fpsmaster.modules.client.AsyncTask
import top.fpsmaster.modules.client.PlayerManager
import top.fpsmaster.modules.config.ConfigManager
import top.fpsmaster.modules.logger.Logger
import top.fpsmaster.modules.music.MusicPlayer
import top.fpsmaster.modules.music.netease.NeteaseApi
import top.fpsmaster.modules.plugin.PluginManager
import top.fpsmaster.ui.click.music.MusicPanel
import top.fpsmaster.ui.click.themes.DarkTheme
import top.fpsmaster.ui.click.themes.LightTheme
import top.fpsmaster.ui.click.themes.Theme
import top.fpsmaster.ui.custom.ComponentsManager
import top.fpsmaster.ui.screens.oobe.OOBEScreen
import top.fpsmaster.utils.GitInfo
import top.fpsmaster.utils.os.FileUtils
import top.fpsmaster.modules.i18n.Language
import top.fpsmaster.utils.thirdparty.github.UpdateChecker
import top.fpsmaster.websocket.client.WsClient
import top.fpsmaster.wrapper.Constants


class FPSMaster {
    var hasOptifine: Boolean = false

    @JvmField
    var loggedIn = false

    var wsClient: WsClient? = null


    private fun initializeFonts() {
        Logger.info("Initializing Fonts...")
        fontManager.load()
    }


    private fun initializeLang() {
        Logger.info("Initializing I18N...")
        i18n.read("zh_cn")
    }

    private fun initializeConfigures() {
        Logger.info("Initializing Config...")
        configManager.loadConfig("default")
        theme = if (themeSlot == "dark") {
            DarkTheme()
        } else {
            LightTheme()
        }
        MusicPlayer.setVolume(configManager.configure.getOrCreate("volume", "1").toFloat())
        NeteaseApi.cookies = FileUtils.readTempValue("cookies")
        MusicPanel.nickname = FileUtils.readTempValue("nickname")
        accountManager.autoLogin()
    }

    private fun initializeMusic() {
        Logger.info("Checking music cache...")
        val dirSize = FileUtils.getDirSize(FileUtils.artists)
        if (dirSize > 1024) {
            FileUtils.artists.delete()
            Logger.info("Cleared img cache")
        }
        Logger.info("Found image: " + dirSize + "mb")
        val dirSize1 = FileUtils.getDirSize(FileUtils.music)
        if (dirSize1 > 2048) {
            FileUtils.music.delete()
            Logger.warn("Cleared music cache")
        }
        Logger.info("Found music: " + dirSize1 + "mb")
    }

    private fun initializeComponents() {
        Logger.info("Initializing component...")
        componentsManager.init()
    }

    private fun initializeCommands() {
        Logger.info("Initializing commands")
        commandManager.init()
    }

    private fun initializePlugins() {
        Logger.info("Start loading plugins")
        plugins.init()
        Logger.info("Loaded ${PluginManager.plugins.size} plugins!")
        Logger.info("Initialized")
    }

    private fun initializeModules(){
        moduleManager.init()
        submitter.init()
    }

    private fun checkOptifine() {
        try {
            Class.forName("optifine.Patcher")
            hasOptifine = true
        } catch (_: ClassNotFoundException) {
        }
    }

    private fun checkUpdate() {
        async.runnable {
            val s = UpdateChecker.getLatestVersion()
            if (s == null) {
                isLatest = false
                updateFailed = true
                return@runnable
            }
            val version = CLIENT_VERSION.replace("v".toRegex(), "").replace("\\.".toRegex(), "").toInt()
            val newVersion = s.replace("v".toRegex(), "").replace("\\.".toRegex(), "").toInt()
            if (s.isNotEmpty()) {
                latest = s
                isLatest = version >= newVersion
            }
        }
    }

    fun initialize() {
        initializeFonts()
        initializeLang()
        initializeMusic()
        initializeModules()
        initializeComponents()
        initializeConfigures()
        initializeCommands()
        initializePlugins()

        checkUpdate()
        checkOptifine()
    }


    fun shutdown() {
        configManager.saveConfig("default")
    }

    companion object {
        const val SERVICE_API = "https://service.fpsmaster.top"
        const val FILE_API = "https://files.fpsmaster.top"

        const val EDITION = Constants.EDITION
        const val COPYRIGHT = "Copyright ©2020-2024  FPSMaster Team  All Rights Reserved."

        @JvmField
        var INSTANCE = FPSMaster()

        @JvmField
        var CLIENT_NAME = "FPSMaster"

        @JvmField
        var CLIENT_VERSION = "v3.2.9-patch-1"

        @JvmField
        var theme: Theme = DarkTheme()

        @JvmField
        var themeSlot = "dark"

        @JvmField
        var moduleManager: ModuleManager = ModuleManager()

        @JvmField
        var fontManager: FontManager = FontManager()

        @JvmField
        var configManager: ConfigManager = ConfigManager()

        @JvmField
        var playerManager: PlayerManager = PlayerManager()

        @JvmField
        var oobeScreen: OOBEScreen = OOBEScreen()

        @JvmField
        var accountManager: AccountManager = AccountManager()

        @JvmField
        var submitter: GlobalSubmitter = GlobalSubmitter()

        @JvmField
        var plugins: PluginManager = PluginManager()

        @JvmField
        var commandManager: CommandManager = CommandManager()

        @JvmField
        var componentsManager: ComponentsManager = ComponentsManager()

        @JvmField
        var i18n: Language = Language()

        @JvmField
        var async = AsyncTask(100)

        @JvmField
        var development = false

        @JvmField
        var debug = false

        @JvmField
        var isLatest = false

        @JvmField
        var updateFailed = false

        @JvmField
        var latest = ""

        private fun checkDevelopment() {
            try {
                Class.forName("net.fabricmc.devlaunchinjector.Main")
                development = true
            }catch (e: Throwable){
                // ignored
            }
        }

        @JvmStatic
        fun getClientTitle(): String {
            checkDevelopment()
            return "$CLIENT_NAME $CLIENT_VERSION ${Constants.VERSION} (${Constants.EDITION}) (${GitInfo.branch} - ${GitInfo.commitIdAbbrev})" + if (development) " - Developer Mode" else ""
        }
    }
}

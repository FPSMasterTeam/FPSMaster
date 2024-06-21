package top.fpsmaster.utils

import net.minecraft.client.settings.GameSettings
import top.fpsmaster.FPSMaster
import java.lang.reflect.Field

class OptifineUtil : Utility() {
    companion object {
        private var ofFastRender: Field? = null

        fun isFastRender(): Boolean {
            if (!FPSMaster.INSTANCE.hasOptifine) return false
            try {
                if (ofFastRender == null) {
                    Class.forName("Config")
                    ofFastRender = GameSettings::class.java.getDeclaredField("ofFastRender")
                }
                ofFastRender!!.setAccessible(true)
                return ofFastRender!!.getBoolean(mc.gameSettings)
            } catch (ignore: ClassNotFoundException) {
            } catch (ignore: IllegalAccessException) {
            } catch (ignore: NoSuchFieldException) {
            }
            return false
        }

        fun setFastRender(value: Boolean) {
            if (!FPSMaster.INSTANCE.hasOptifine) return
            try {
                if (ofFastRender == null) {
                    Class.forName("Config")
                    ofFastRender = GameSettings::class.java.getDeclaredField("ofFastRender")
                }
                ofFastRender!!.setAccessible(true)
                ofFastRender!!.setBoolean(mc.gameSettings, value)
            } catch (ignore: ClassNotFoundException) {
            } catch (ignore: IllegalAccessException) {
            } catch (ignore: NoSuchFieldException) {
            }
        }
    }
}
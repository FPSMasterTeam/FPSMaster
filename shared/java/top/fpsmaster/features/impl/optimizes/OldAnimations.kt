package top.fpsmaster.features.impl.optimizes

import top.fpsmaster.features.manager.Category
import top.fpsmaster.features.manager.Module
import top.fpsmaster.features.settings.impl.BooleanSetting
import top.fpsmaster.features.settings.impl.NumberSetting

class OldAnimations : Module("OldAnimations", Category.OPTIMIZE) {
    init {
        addSettings(noShield, oldRod, oldBow, oldSwing, oldDamage, oldUsing, blockHit, oldBlock, x, y, z)
    }

    override fun onEnable() {
        super.onEnable()
        using = true
    }

    override fun onDisable() {
        super.onDisable()
        using = false
    }

    companion object {
        @JvmField
        var noShield = BooleanSetting("NoShield", true)
        @JvmField
        var oldBlock = BooleanSetting("OldBlock", true)
        @JvmField
        var oldRod = BooleanSetting("OldRod", true)
        @JvmField
        var oldBow = BooleanSetting("OldBow", true)
        @JvmField
        var oldSwing = BooleanSetting("OldSwing", true)
        @JvmField
        var oldUsing = BooleanSetting("OldUsing", true)
        @JvmField
        var oldDamage = BooleanSetting("OldDamage", true)
        @JvmField
        var blockHit = BooleanSetting("BlockHit", true)
        @JvmField
        var x = NumberSetting("X", 0, -1, 1, 0.01)
        @JvmField
        var y = NumberSetting("Y", 0, -1, 1, 0.01)
        @JvmField
        var z = NumberSetting("Z", 0, -1, 1, 0.01)
        @JvmField
        var using = false
    }
}

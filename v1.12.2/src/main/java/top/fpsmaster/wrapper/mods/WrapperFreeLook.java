package top.fpsmaster.wrapper.mods;

import org.lwjgl.opengl.Display;
import top.fpsmaster.features.impl.render.FreeLook;

import static top.fpsmaster.features.impl.render.FreeLook.*;
import static top.fpsmaster.utils.Utility.*;

public class WrapperFreeLook {


    public static float getCameraYaw() {
        if (perspectiveToggled) {
            return cameraYaw;
        } else {
            assert mc.getRenderViewEntity() != null;
            return mc.getRenderViewEntity().rotationYaw;
        }
    }

    public static float getCameraPitch() {
        if (perspectiveToggled) {
            return cameraPitch;
        } else {
            assert mc.getRenderViewEntity() != null;
            return mc.getRenderViewEntity().rotationPitch;
        }
    }

    public static float getCameraPrevYaw() {
        if (perspectiveToggled) {
            return cameraYaw;
        } else {
            assert mc.getRenderViewEntity() != null;
            return mc.getRenderViewEntity().prevRotationYaw;
        }
    }

    public static float getCameraPrevPitch() {
        if (perspectiveToggled) {
            return cameraPitch;
        } else {
            assert mc.getRenderViewEntity() != null;
            return mc.getRenderViewEntity().prevRotationPitch;
        }
    }

    public static boolean overrideMouse() {
        if (mc.inGameHasFocus && Display.isActive() && FreeLook.using && perspectiveToggled) {
            mc.mouseHelper.mouseXYChange();
            float f1 = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
            float f2 = f1 * f1 * f1 * 8.0f;
            float f3 = mc.mouseHelper.deltaX * f2;
            float f4 = mc.mouseHelper.deltaY * f2;
            cameraYaw += f3 * 0.15f;
            cameraPitch += f4 * 0.15f;
            if (cameraPitch > 90.0f) {
                cameraPitch = 90.0f;
            }
            if (cameraPitch < -90.0f) {
                cameraPitch = -90.0f;
            }
            return true;
        }
        return false;
    }
}

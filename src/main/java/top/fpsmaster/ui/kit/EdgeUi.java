package top.fpsmaster.ui.kit;

import net.minecraft.client.gui.GuiScreen;
import top.fpsmaster.features.impl.interfaces.ClientSettings;
import top.fpsmaster.features.settings.impl.BindKeys;
import top.fpsmaster.utils.render.gui.ScaledGuiScreen;
import top.fpsmaster.prism.input.FrameInput;
import top.fpsmaster.prism.input.Keys;
import top.fpsmaster.prism.theme.Theme;
import top.fpsmaster.prism.widget.UiFrame;

/**
 * Binds a {@link UiFrame} to the current Edge paint pass.
 */
public final class EdgeUi {
    private static UiFrame current;
    private static final FrameInput fallback = new FrameInput();
    /**
     * 这一帧是不是宿主屏幕开的。键鼠缓冲只有喂过 {@link #keyTyped} 的那一帧有权清：
     * HUD 覆盖层每帧都 begin/end 一次、又排在 drawScreen 前面，无脑清会把玩家这一帧
     * 敲的键冲掉——绑定按钮和文本框双双点不动，纯鼠标的开关滑条却照常。
     */
    private static boolean screenFrame;

    private EdgeUi() {
    }

    public static Theme theme() {
        boolean light = ClientSettings.theme.getValue() == 1;
        return Theme.of(light, ClientSettings.blur.getValue());
    }

    public static void begin(ScaledGuiScreen screen) {
        float w = screen == null ? 0f : screen.guiWidth;
        float h = screen == null ? 0f : screen.guiHeight;
        // Shift 是修饰键：Prism 的文本框用 isKeyDown(SHIFT) 决定方向键/Home/End 是否扩选。
        fallback.setKeyDown(Keys.SHIFT, GuiScreen.isShiftKeyDown());
        current = new UiFrame(new EdgeHost(screen, fallback, w, h), theme());
        screenFrame = true;
    }

    public static void beginOverlay(float guiWidth, float guiHeight) {
        current = new UiFrame(new EdgeHost(null, fallback, guiWidth, guiHeight), theme());
        screenFrame = false;
    }

    public static void end() {
        if (screenFrame) {
            fallback.endFrame();
        }
        screenFrame = false;
        EdgeCanvas.clearPanelClip();
        current = null;
    }

    /**
     * Extra GL clip intersected with every {@code pushClip}. Used so ClickGUI module
     * settings cannot paint past the panel when an expanded card is taller than the window.
     */
    public static void clipPanel(float x, float y, float w, float h) {
        EdgeCanvas.setPanelClip(x, y, w, h);
    }

    public static UiFrame frame() {
        if (current == null) {
            throw new IllegalStateException("EdgeUi.begin() not called for this frame");
        }
        return current;
    }

    public static boolean hasFrame() {
        return current != null;
    }

    public static void keyTyped(char typedChar, int keyCode) {
        fallback.pressRawKey(keyCode);
        // Ctrl+A/C/X/V（macOS 上 GuiScreen.isCtrlKeyDown 认的是 Cmd）是编辑快捷键：
        // 只发虚拟键，不再把字母 type 进去——macOS 上 Cmd+V 的 typedChar 就是 'v'。
        int shortcut = editingShortcut(keyCode);
        if (shortcut != 0) {
            fallback.pressKey(shortcut);
            return;
        }
        if (!Character.isISOControl(typedChar)) {
            fallback.type(String.valueOf(typedChar));
        }
        switch (keyCode) {
            case org.lwjgl.input.Keyboard.KEY_BACK:
                fallback.pressKey(Keys.BACKSPACE);
                break;
            case org.lwjgl.input.Keyboard.KEY_RETURN:
            case org.lwjgl.input.Keyboard.KEY_NUMPADENTER:
                fallback.pressKey(Keys.ENTER);
                break;
            case org.lwjgl.input.Keyboard.KEY_ESCAPE:
                fallback.pressKey(Keys.ESCAPE);
                break;
            case org.lwjgl.input.Keyboard.KEY_LEFT:
                fallback.pressKey(Keys.LEFT);
                break;
            case org.lwjgl.input.Keyboard.KEY_RIGHT:
                fallback.pressKey(Keys.RIGHT);
                break;
            case org.lwjgl.input.Keyboard.KEY_DELETE:
                fallback.pressKey(Keys.DELETE);
                break;
            case org.lwjgl.input.Keyboard.KEY_HOME:
                fallback.pressKey(Keys.HOME);
                break;
            case org.lwjgl.input.Keyboard.KEY_END:
                fallback.pressKey(Keys.END);
                break;
            default:
                break;
        }
    }

    private static int editingShortcut(int keyCode) {
        if (!GuiScreen.isCtrlKeyDown() || GuiScreen.isShiftKeyDown() || GuiScreen.isAltKeyDown()) {
            return 0;
        }
        switch (keyCode) {
            case org.lwjgl.input.Keyboard.KEY_A:
                return Keys.SELECT_ALL;
            case org.lwjgl.input.Keyboard.KEY_C:
                return Keys.COPY;
            case org.lwjgl.input.Keyboard.KEY_X:
                return Keys.CUT;
            case org.lwjgl.input.Keyboard.KEY_V:
                return Keys.PASTE;
            default:
                return 0;
        }
    }

    /**
     * 鼠标中键/侧键当一次按键喂给绑定界面。左/右键不喂——「点一下开始捕获」本身就是左键，
     * 喂进去会同一帧把自己绑掉。
     */
    public static void mousePressed(int button) {
        if (BindKeys.isBindableButton(button)) {
            fallback.pressRawKey(BindKeys.toWire(BindKeys.mouseCode(button)));
        }
    }
}

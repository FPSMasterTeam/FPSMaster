package top.fpsmaster.forge.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fpsmaster.FPSMaster;
import top.fpsmaster.forge.api.IMinecraft;
import top.fpsmaster.event.EventDispatcher;
import top.fpsmaster.event.events.EventKey;
import top.fpsmaster.event.events.EventMouseClick;
import top.fpsmaster.event.events.EventTick;
import top.fpsmaster.features.impl.optimizes.Performance;
import top.fpsmaster.features.impl.utility.PreventBanning;
import top.fpsmaster.interfaces.ProviderManager;
import top.fpsmaster.utils.render.Render2DUtils;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Iterator;

import static top.fpsmaster.FPSMaster.getClientTitle;

@Mixin(Minecraft.class)
@Implements(@Interface(iface = IMinecraft.class, prefix = "fpsmaster$"))
public abstract class MixinMinecraft implements IMinecraft {
    @Final
    @Shadow
    private Timer timer;

    @Shadow
    public GameSettings gameSettings;

    @Shadow
    public EntityRenderer entityRenderer;

    @Shadow
    public abstract Entity getRenderViewEntity();

    @Shadow
    public RenderGlobal renderGlobal;
    @Shadow
    public EntityPlayerSP thePlayer;

    @Shadow
    public GuiIngame ingameGUI;
    @Shadow
    public GuiScreen currentScreen;

    @Shadow
    public PlayerControllerMP playerController;

    @Shadow
    public abstract void displayGuiScreen(@Nullable GuiScreen guiScreenIn);


    @Shadow
    public abstract NetHandlerPlayClient getNetHandler();

    @Shadow
    private int rightClickDelayTimer;
    @Shadow
    public boolean inGameHasFocus;

    @Shadow
    public abstract void updateDisplay();

    @Mutable
    @Final
    @Shadow
    private Session session;

    @Override
    public Session arch$getSession() {
        return session;
    }

    @Override
    public void arch$setSession(Session session) {
        this.session = session;
    }

    @Override
    public void arch$setLeftClickCounter(int leftClickCounter) {
        this.leftClickCounter = leftClickCounter;
    }

    @Override
    public void arch$setRightClickDelayTimer(int rightClickDelayTimer) {
        this.rightClickDelayTimer = rightClickDelayTimer;
    }

    /**
     * @author SuperSkidder
     * @reason splash
     */
    @Overwrite
    public void drawSplashScreen(TextureManager textureManagerInstance) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int i = sr.getScaleFactor();
        Framebuffer framebuffer = new Framebuffer(sr.getScaledWidth() * i, sr.getScaledHeight() * i, true);
        framebuffer.bindFramebuffer(false);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, sr.getScaledWidth(), sr.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.resetColor();

        Gui.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(20, 20, 20).getRGB());
        float width = 400;
        float height = 250;
        Render2DUtils.drawImage(new ResourceLocation("client/textures/ui/splash.png"), sr.getScaledWidth() / 2f - width / 2, sr.getScaledHeight() / 2f - height / 2, width, height, -1);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(sr.getScaledWidth() * i, sr.getScaledHeight() * i);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        updateDisplay();
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    public void shutdown(CallbackInfo ci) {
        FPSMaster.INSTANCE.shutdown();
    }


    @Override
    public Timer arch$getTimer() {
        return timer;
    }

    @Inject(method = "runTick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        EventDispatcher.dispatchEvent(new EventTick());
    }

    @Shadow
    protected abstract void sendClickBlockToController(boolean leftClick);

    @Shadow
    protected abstract void clickMouse();

    @Shadow
    protected abstract void rightClickMouse();


    /**
     * @author SuperSkidder
     * @reason fps Limit
     */
    @Overwrite
    public int getLimitFramerate() {
        if (ProviderManager.mcProvider.getCurrentScreen() != null && ProviderManager.mcProvider.getWorld() == null)
            return 60;
        return (Display.isActive()) ? this.gameSettings.limitFramerate : Performance.using ? Performance.fpsLimit.getValue().intValue() : this.gameSettings.limitFramerate;
    }

    /**
     * FastLoad
     */
    @Redirect(method = "launchIntegratedServer", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
    public void gc1() {
        if (!Performance.using || !Performance.fastLoad.getValue()) {
            System.gc();
        }
    }

    @Redirect(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
    public void gc2() {
        if (!Performance.using || !Performance.fastLoad.getValue()) {
            System.gc();
        }
    }


    @Inject(method = "rightClickMouse", at = @At("HEAD"), cancellable = true)
    public void preventBanning(CallbackInfo ci) {
        if (PreventBanning.using && !PreventBanning.canBlock())
            ci.cancel();
    }

    @Shadow
    protected abstract void middleClickMouse();

    @Shadow
    private int leftClickCounter;

    @Inject(method = "clickMouse", at = @At("RETURN"))
    public void onClickMouse(CallbackInfo ci) {
        leftClickCounter = 0;
    }

    @Inject(method = "clickMouse", at = @At("HEAD"))
    public void cpsl(CallbackInfo ci) {
        EventDispatcher.dispatchEvent(new EventMouseClick(0));
    }
    @Inject(method = "rightClickMouse", at = @At("HEAD"))
    public void cpsr(CallbackInfo ci) {
        EventDispatcher.dispatchEvent(new EventMouseClick(1));
    }
    @Inject(method = "dispatchKeypresses", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;getEventKey()I", shift = At.Shift.AFTER))
    public void keyEvent(CallbackInfo ci) {
        EventKey key = new EventKey(Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey());
        EventDispatcher.dispatchEvent(key);
    }
    @Inject(method = "createDisplay", at = @At(value = "RETURN"))
    public void setTitle(CallbackInfo ci) {
        Display.setTitle(getClientTitle());
    }
}

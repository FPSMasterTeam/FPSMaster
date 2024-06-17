package top.fpsmaster.ui.screens.mainmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import top.fpsmaster.FPSMaster;
import top.fpsmaster.interfaces.ProviderManager;
import top.fpsmaster.ui.screens.oobe.GuiLogin;
import top.fpsmaster.ui.screens.account.GuiWaiting;
import top.fpsmaster.ui.screens.plugins.GuiPlugins;
import top.fpsmaster.utils.render.Render2DUtils;
import top.fpsmaster.wrapper.TextFormattingProvider;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

import static top.fpsmaster.utils.Utility.isHovered;

public class MainMenu extends GuiScreen {
    GuiButton singlePlayer = new GuiButton("mainmenu.single", () -> {
        ProviderManager.mainmenuProvider.showSinglePlayer(this);
    });
    GuiButton multiPlayer = new GuiButton("mainmenu.multi", () -> {
        mc.displayGuiScreen(new GuiMultiplayer(this));
    });
    GuiButton options = new GuiButton("mainmenu.settings", () -> {
        mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
    });

    GuiButton exit = new GuiButton("X", () -> {
        mc.shutdown();
    });

    String latest = "获取版本更新失败";
    String welcome = "获取版本更新失败";
    boolean needUpdate = false;

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        ProviderManager.mainmenuProvider.initGui();
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (FPSMaster.INSTANCE.configManager.configure.getOrCreate("firstStart", "true").equals("true")) {
            Minecraft.getMinecraft().displayGuiScreen(FPSMaster.INSTANCE.oobeScreen);
        }
        ProviderManager.mainmenuProvider.renderSkybox(mouseX, mouseY, partialTicks, this.width, this.height, this.zLevel);

        Render2DUtils.drawRect(0, 0, this.width, this.height, new Color(26, 59, 109, 60));

        int stringWidth = FPSMaster.INSTANCE.fontManager.s16.getStringWidth(mc.getSession().getUsername());
        if (isHovered(10, 10, 80, 20, mouseX, mouseY)) {
            Render2DUtils.drawOptimizedRoundedRect(10, 10, 30 + stringWidth, 20, new Color(0, 0, 0, 100));
            if (Mouse.isButtonDown(0)) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiWaiting());
            }
        } else {
            Render2DUtils.drawOptimizedRoundedRect(10, 10, 30 + stringWidth, 20, new Color(0, 0, 0, 60));
        }
        Render2DUtils.drawImage(new ResourceLocation("client/gui/screen/avatar.png"), 14, 15, 10, 10, -1);
        FPSMaster.INSTANCE.fontManager.s16.drawString(mc.getSession().getUsername(), 28, 16, new Color(255, 255, 255).getRGB());
        Render2DUtils.drawImage(new ResourceLocation("client/gui/logo.png"), width / 2f - 153 / 4f, height / 2f - 100, 153 / 2f, 67, -1);

        int x = width / 2 - 50;
        int y = height / 2 - 30;

        singlePlayer.render(x, y, 100, 20, mouseX, mouseY);
        multiPlayer.render(x, y + 24, 100, 20, mouseX, mouseY);
        options.render(x, y + 48, 70, 20, mouseX, mouseY);
        exit.render(x + 74, y + 48, 26, 20, mouseX, mouseY);


        float w = FPSMaster.INSTANCE.fontManager.s16.getStringWidth("Copyright Mojang AB. Do not distribute!");
        FPSMaster.INSTANCE.fontManager.s16.drawString("Copyright Mojang AB. Do not distribute!", this.width - w - 4, this.height - 14, new Color(255, 255, 255, 50).getRGB());
        if (FPSMaster.INSTANCE.loggedIn) {
            welcome = TextFormattingProvider.getGreen() + FPSMaster.INSTANCE.i18n.get("mainmenu.welcome") + FPSMaster.INSTANCE.configManager.configure.getOrCreate("username", "");
        } else {
            welcome = TextFormattingProvider.getRed() + String.valueOf(TextFormattingProvider.getBold()) + FPSMaster.INSTANCE.i18n.get("mainmenu.notlogin");
        }
        FPSMaster.INSTANCE.fontManager.s16.drawString(welcome, 4, this.height - 52, new Color(255, 255, 255).getRGB());

        if (FPSMaster.isLatest) {
            latest = TextFormattingProvider.getGreen() + FPSMaster.INSTANCE.i18n.get("mainmenu.latest");
        } else {
            latest = TextFormattingProvider.getRed() + String.valueOf(TextFormattingProvider.getBold()) + FPSMaster.INSTANCE.i18n.get("mainmenu.notlatest") + FPSMaster.latest + FPSMaster.INSTANCE.i18n.get("mainmenu.toupdate");
            needUpdate = true;
        }
        FPSMaster.INSTANCE.fontManager.s16.drawString(latest, 4, this.height - 40, new Color(255, 255, 255).getRGB());

        Render2DUtils.drawRect(0, 0, 0, 0, -1);
        FPSMaster.INSTANCE.fontManager.s16.drawString(FPSMaster.COPYRIGHT, 4, this.height - 14, new Color(255, 255, 255).getRGB());
        FPSMaster.INSTANCE.fontManager.s16.drawString(FPSMaster.CLIENT_NAME + " Client " + FPSMaster.CLIENT_VERSION + " (Minecraft " + FPSMaster.EDITION + ")", 4, this.height - 28, new Color(255, 255, 255).getRGB());

        // plugins
        if (isHovered(this.width - 16, 15, 12, 12, mouseX, mouseY)) {
            Render2DUtils.drawImage(new ResourceLocation("client/gui/screen/plugin.png"), this.width - 20, 12, 12, 12, -1);
        } else {
            Render2DUtils.drawImage(new ResourceLocation("client/gui/screen/plugin.png"), this.width - 20, 12, 12, 12, new Color(200,200,200));
        }


        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        singlePlayer.mouseClick(mouseX, mouseY, mouseButton);
        multiPlayer.mouseClick(mouseX, mouseY, mouseButton);
        multiPlayer.mouseClick(mouseX, mouseY, mouseButton);
        options.mouseClick(mouseX, mouseY, mouseButton);
        exit.mouseClick(mouseX, mouseY, mouseButton);
        float uw = FPSMaster.INSTANCE.fontManager.s16.getStringWidth(latest);
        float nw = FPSMaster.INSTANCE.fontManager.s16.getStringWidth(latest);

        if (mouseButton == 0) {
            if (isHovered(4, this.height - 52, nw, 14, mouseX, mouseY)) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiLogin());
            }

            if (isHovered(4, this.height - 40, uw, 14, mouseX, mouseY) && needUpdate) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(new URI("https://fpsmaster.top/download"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (isHovered(this.width - 16, 15, 12, 12, mouseX, mouseY)) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiPlugins());
            }
        }
    }
}

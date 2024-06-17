package top.fpsmaster.forge.mixin;

import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import top.fpsmaster.FPSMaster;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

@Mixin(GuiPlayerTabOverlay.class)
public abstract class MixinGuiPlayerOverlay {

    Minecraft mc = Minecraft.getMinecraft();


    @Shadow
    public abstract String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn);

    @Shadow
    private IChatComponent header;

    @Shadow
    private IChatComponent footer;

    @Shadow
    protected abstract void drawScoreboardValues(ScoreObjective objective, int i, String name, int j, int k, NetworkPlayerInfo info);

    @Shadow
    protected abstract void drawPing(int i, int j, int k, NetworkPlayerInfo networkPlayerInfoIn);
}

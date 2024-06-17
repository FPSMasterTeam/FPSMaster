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
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import top.fpsmaster.FPSMaster;
import top.fpsmaster.modules.client.PlayerManager;
import top.fpsmaster.utils.render.Render2DUtils;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

@Mixin(GuiPlayerTabOverlay.class)
public abstract class MixinGuiPlayerOverlay {

    @Final
    @Shadow
    private static Ordering<NetworkPlayerInfo> ENTRY_ORDERING;

    @Shadow
    public abstract String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn);

    @Shadow
    private ITextComponent header;

    @Shadow
    private ITextComponent footer;

    @Shadow
    protected abstract void drawScoreboardValues(ScoreObjective objective, int i, String name, int j, int k, NetworkPlayerInfo info);

    @Shadow
    protected abstract void drawPing(int i, int j, int k, NetworkPlayerInfo networkPlayerInfoIn);

    }

package top.fpsmaster.forge.mixin;

import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.fpsmaster.forge.api.INetworkPlayerInfo;

import java.util.Map;

@Mixin(NetworkPlayerInfo.class)
public class MixinNetworkPlayerInfo implements INetworkPlayerInfo {

    @Shadow
    Map<MinecraftProfileTexture.Type, ResourceLocation> playerTextures = Maps.newEnumMap(MinecraftProfileTexture.Type.class);

    @Shadow
    private String skinType;

    @Override
    public Map<MinecraftProfileTexture.Type, ResourceLocation> getTextures() {
        return playerTextures;
    }


    @Override
    public String getType() {
        return skinType;
    }

    @Override
    public void setTextures(Map<MinecraftProfileTexture.Type, ResourceLocation> skin) {
        this.playerTextures = skin;
    }

    @Override
    public void setType(String skinType) {
        this.skinType = skinType;
    }
}

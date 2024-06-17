package top.fpsmaster.forge.api;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public interface INetworkPlayerInfo {
    Map<MinecraftProfileTexture.Type, ResourceLocation> getTextures();
    String getType();
    void setTextures(Map<MinecraftProfileTexture.Type, ResourceLocation> skin);
    void setType(String skinType);
}

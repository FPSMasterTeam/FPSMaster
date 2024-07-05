package top.fpsmaster.forge.mixin;

import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.resources.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import top.fpsmaster.FPSMaster;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    /**
     * @author SuperSkidder
     * @reason change client title
     */
    @Overwrite
    private String createTitle() {
        StringBuilder stringbuilder = new StringBuilder(FPSMaster.Companion.getClientTitle());
        stringbuilder.append(" ");
        stringbuilder.append(SharedConstants.getCurrentVersion().getName());
        ClientPacketListener clientpacketlistener = Minecraft.getInstance().getConnection();
        if (clientpacketlistener != null && clientpacketlistener.getConnection().isConnected()) {
            stringbuilder.append(" - ");
            if (this.singleplayerServer != null && !this.singleplayerServer.isPublished()) {
                stringbuilder.append(I18n.get("title.singleplayer", new Object[0]));
            } else if (this.isConnectedToRealms()) {
                stringbuilder.append(I18n.get("title.multiplayer.realms", new Object[0]));
            } else if (this.singleplayerServer != null || this.currentServer != null && this.currentServer.isLan()) {
                stringbuilder.append(I18n.get("title.multiplayer.lan", new Object[0]));
            } else {
                stringbuilder.append(I18n.get("title.multiplayer.other", new Object[0]));
            }
        }

        return stringbuilder.toString();
    }
}

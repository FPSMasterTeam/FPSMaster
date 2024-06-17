package top.fpsmaster.wrapper.packets;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import org.jetbrains.annotations.NotNull;
import top.fpsmaster.interfaces.packets.IPacketTimeUpdate;

public class SPacketTimeUpdateProvider implements IPacketTimeUpdate {
    public boolean isPacket(@NotNull Object packet) {
        return packet instanceof SPacketTimeUpdate;
    }
}

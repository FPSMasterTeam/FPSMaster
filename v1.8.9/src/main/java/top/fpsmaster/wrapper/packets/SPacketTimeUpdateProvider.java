package top.fpsmaster.wrapper.packets;

import net.minecraft.network.play.server.S03PacketTimeUpdate;
import top.fpsmaster.interfaces.packets.IPacketTimeUpdate;

public class SPacketTimeUpdateProvider implements IPacketTimeUpdate {
    public boolean isPacket(Object packet) {
        return packet instanceof S03PacketTimeUpdate;
    }
}

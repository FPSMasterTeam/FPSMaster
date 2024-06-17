package top.fpsmaster.websocket.data.message.client;

import com.google.gson.annotations.SerializedName;
import top.fpsmaster.websocket.data.message.Packet;
import top.fpsmaster.websocket.data.message.PacketType;

public class PlayerInfoPacket extends Packet {
    @SerializedName("playerName")
    public String playerName;
    @SerializedName("UUID")
    public String UUID;
    public PlayerInfoPacket(String playerName, String UUID) {
        super(PacketType.CLIENT_PLAYER_INFO);
        this.playerName = playerName;
        this.UUID = UUID;
    }
}

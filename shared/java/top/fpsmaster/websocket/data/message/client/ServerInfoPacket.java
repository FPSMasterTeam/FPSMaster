package top.fpsmaster.websocket.data.message.client;

import com.google.gson.annotations.SerializedName;
import top.fpsmaster.websocket.data.message.Packet;
import top.fpsmaster.websocket.data.message.PacketType;

public class ServerInfoPacket extends Packet {

    @SerializedName("serverIP")
    public String serverIP;

    public ServerInfoPacket(String serverIP) {
        super(PacketType.CLIENT_SERVER_INFO);
        this.serverIP = serverIP;
    }
}

package top.fpsmaster.websocket.data.message.server;

import com.google.gson.annotations.SerializedName;
import top.fpsmaster.websocket.data.message.Packet;
import top.fpsmaster.websocket.data.message.PacketType;
import top.fpsmaster.websocket.data.message.client.FetchInfoPacket;

public class SDataPacket extends Packet {
    @SerializedName("type")
    public FetchInfoPacket.InfoType type;
    @SerializedName("data")
    public String data;

    public SDataPacket(FetchInfoPacket.InfoType type, String data) {
        super(PacketType.SERVER_DATA);
        this.type = type;
        this.data = data;
    }
}

package top.fpsmaster.websocket.data.message;

import com.google.gson.annotations.SerializedName;
import top.fpsmaster.websocket.data.WsUtilKt;

import static top.fpsmaster.websocket.data.WsUtilKt.parseJson;


public class Packet {
    @SerializedName("type")
    public PacketType type;

    public Packet(PacketType type) {
        this.type = type;
    }

    public String toJson() {
        return WsUtilKt.toJson(this);
    }

    public Packet parse(String json) {
        return (Packet) parseJson(json, this.getClass());
    }

    public static Packet parsePacket(String json, Class packet) {
        return (Packet) parseJson(json, packet);
    }
}

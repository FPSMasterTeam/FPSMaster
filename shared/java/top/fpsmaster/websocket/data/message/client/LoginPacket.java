package top.fpsmaster.websocket.data.message.client;

import com.google.gson.annotations.SerializedName;
import top.fpsmaster.websocket.data.message.Packet;
import top.fpsmaster.websocket.data.message.PacketType;

public class LoginPacket extends Packet {
    @SerializedName("username")
    public String username;
    @SerializedName("token")
    public String token;

    public LoginPacket(String username, String token) {
        super(PacketType.CLIENT_LOGIN);
        this.username = username;
        this.token = token;
    }
}

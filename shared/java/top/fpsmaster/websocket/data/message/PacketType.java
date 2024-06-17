package top.fpsmaster.websocket.data.message;

import com.google.gson.annotations.SerializedName;

public enum PacketType {
    // CLIENT_SIDE
    @SerializedName("CLIENT_LOGIN")
    CLIENT_LOGIN,
    @SerializedName("CLIENT_MESSAGE")
    CLIENT_MESSAGE,
    @SerializedName("CLIENT_DIRECT_MSG")
    CLIENT_DIRECT_MSG,
    @SerializedName("CLIENT_SERVER_INFO")
    CLIENT_SERVER_INFO,
    @SerializedName("CLIENT_PLAYER_INFO")
    CLIENT_PLAYER_INFO,
    @SerializedName("CLIENT_COSMETIC_INFO")
    CLIENT_COSMETIC_INFO,

    CLIENT_FETCH, // SERVER_SIDE
    SERVER_DATA, @SerializedName("SERVER_MESSAGE")
    SERVER_MESSAGE
}

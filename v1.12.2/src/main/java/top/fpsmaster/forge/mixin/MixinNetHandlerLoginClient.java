package top.fpsmaster.forge.mixin;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.util.CryptManager;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.*;
import top.fpsmaster.event.EventDispatcher;
import top.fpsmaster.event.events.EventJoinServer;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.PublicKey;

import static top.fpsmaster.utils.Utility.mc;

@Mixin(NetHandlerLoginClient.class)
public abstract class MixinNetHandlerLoginClient {

    @Final
    @Shadow
    private NetworkManager networkManager;
    @Final
    @Shadow
    private static Logger LOGGER;
    @Unique
    private boolean isHyt = false;


    @Shadow
    protected abstract MinecraftSessionService getSessionService();


    /**
     * @author SuperSkidder
     * @reason for plugin
     */
    @Overwrite
    public void handleEncryptionRequest(SPacketEncryptionRequest packetIn) {
        final SecretKey secretkey = CryptManager.createNewSharedKey();
        String s = packetIn.getServerId();
        PublicKey publickey = packetIn.getPublicKey();
        String s1 = (new BigInteger(CryptManager.getServerIdHash(s, publickey, secretkey))).toString(16);
        EventJoinServer eventJoinServer = new EventJoinServer(s1);
        EventDispatcher.dispatchEvent(eventJoinServer);
        isHyt = eventJoinServer.getCancel();
        System.out.println("isHyt: " + isHyt);
        if (mc.getCurrentServerData() != null && mc.getCurrentServerData().isOnLAN()) {
            try {
                this.getSessionService().joinServer(mc.getSession().getProfile(), mc.getSession().getToken(), s1);
            } catch (AuthenticationException var10) {
                LOGGER.warn("Couldn't connect to auth servers but will continue to join LAN");
            }
        } else {
            if (!isHyt) {
                try {
                    this.getSessionService().joinServer(mc.getSession().getProfile(), mc.getSession().getToken(), s1);
                } catch (AuthenticationUnavailableException var7) {
                    this.networkManager.closeChannel(new TextComponentTranslation("disconnect.loginFailedInfo", new TextComponentTranslation("disconnect.loginFailedInfo.serversUnavailable")));
                    return;
                } catch (InvalidCredentialsException var8) {
                    this.networkManager.closeChannel(new TextComponentTranslation("disconnect.loginFailedInfo", new TextComponentTranslation("disconnect.loginFailedInfo.invalidSession")));
                    return;
                } catch (AuthenticationException var9) {
                    this.networkManager.closeChannel(new TextComponentTranslation("disconnect.loginFailedInfo", var9.getMessage()));
                    return;
                }
            }
        }
        isHyt = false;
        this.networkManager.sendPacket(new CPacketEncryptionResponse(secretkey, publickey, packetIn.getVerifyToken()), new GenericFutureListener<Future<? super Void>>() {
            public void operationComplete(Future<? super Void> p_operationComplete_1_) throws Exception {
                networkManager.enableEncryption(secretkey);
            }
        });
    }
}

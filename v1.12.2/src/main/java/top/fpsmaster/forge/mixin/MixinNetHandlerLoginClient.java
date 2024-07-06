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


    @Shadow
    protected abstract MinecraftSessionService getSessionService();
}

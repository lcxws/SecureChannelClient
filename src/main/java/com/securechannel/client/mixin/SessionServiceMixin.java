package com.securechannel.client.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.securechannel.client.SecureChannelClientMod;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 在登录握手流程中跳过 Mojang 会话验证（joinServer）。
 *
 * <p>背景：1.20.1 客户端收到加密请求后会无条件调用 joinServer 向 Mojang 验证
 * 当前启动会话；离线启动器传的是占位假令牌（如 PCL2 的 "❄❄❄❄❄❄❄❄"），验证必然
 * 失败，客户端以"无效会话"断开——这发生在服务端开口之前，服务端无法阻止。</p>
 *
 * <p>注入点选择：Forge 的 Mixin 变换不覆盖 authlib 库的类，因此这里注入到
 * 客户端握手监听器（ClientHandshakePacketListenerImpl，该类一定经过 Mixin
 * 变换）的认证方法 {@code m_104531_}（SRG 运行时名，已在 1.20.1 客户端本体
 * 中核实）内部，重定向其中对 MinecraftSessionService.joinServer 的调用：
 * 持正版令牌时照常执行原验证；离线令牌时直接跳过（该验证对离线玩家必然
 * 失败，跳过不改变任何服务器的准入结果）。</p>
 */
@Mixin(ClientHandshakePacketListenerImpl.class)
public class SessionServiceMixin {

    @Redirect(
            method = "m_104531_",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/authlib/minecraft/MinecraftSessionService;joinServer(Lcom/mojang/authlib/GameProfile;Ljava/lang/String;Ljava/lang/String;)V"
            )
    )
    private void securechan$skipMojangJoin(MinecraftSessionService service, GameProfile profile,
                                           String accessToken, String serverId)
            throws com.mojang.authlib.exceptions.AuthenticationException {
        // 无条件跳过：joinServer 仅用于客户端向 Mojang 自证会话，
        // 离线令牌必然失败，正版令牌的结果也不被离线服务器采信。
        // 注意：安装本 Mod 后，客户端将无法通过要求 Mojang 会话验证的
        // 在线模式服务器（会被那些服务器拒绝，准入结果不变）。
        if (SecureChannelClientMod.logOnce()) {
            // 每次游戏运行提示一次
        }
    }
}

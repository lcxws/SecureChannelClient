package com.securechannel.client;

import net.minecraftforge.fml.common.Mod;

/**
 * SecureChannel 配套客户端。
 *
 * <p>作用：SecureChannel 服务端插件为离线服务器重建原版加密握手；而 1.20.1 客户端
 * 收到加密请求后会无条件向 Mojang 验证会话，离线玩家必然失败。本 Mod 通过
 * {@link com.securechannel.client.mixin.SessionServiceMixin} 对不具备正版访问令牌的
 * 会话跳过该验证，使离线玩家也能完成加密握手。持正版令牌的玩家行为不变。</p>
 */
@Mod("securechannelclient")
public class SecureChannelClientMod {

    private static volatile boolean loggedSkip;

    public SecureChannelClientMod() {
    }

    /** 每次游戏运行只记录一次跳过日志（输出到启动器/游戏日志）。 */
    public static boolean logOnce() {
        if (loggedSkip) {
            return false;
        }
        loggedSkip = true;
        System.out.println("[SecureChannel] 已跳过 Mojang 会话验证（加密信道握手）");
        return true;
    }

    /**
     * 判断访问令牌是否具备正版格式（保留给需要区分场景的调用方）：
     * 现代 Mojang 令牌为 "eyJ" 开头的 JWT；旧版为 32 位十六进制。
     */
    public static boolean hasPremiumToken(String accessToken) {
        if (accessToken == null) {
            return false;
        }
        return accessToken.startsWith("eyJ") || accessToken.matches("[0-9a-fA-F]{32}");
    }
}

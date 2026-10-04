# SecureChannelClient

[SecureChannel](https://github.com/lcxws/SecureChannel)（离线模式服务器原版登录加密插件）的**配套客户端 Forge Mod**，适用于 Minecraft **1.20.1 Forge**。

## 为什么需要它

1.20.1 客户端在收到服务器的加密请求后，会**无条件**向 Mojang 会话服务器验证当前启动会话
（`joinServer`）。离线（破解）启动器传的是占位假令牌（如 PCL2 的 `❄❄❄❄❄❄❄❄`；
正版为 `eyJ` 开头的 JWT），这一步注定失败，玩家会以"无效会话"断开——
这与服务器是否开启 online-mode 无关，服务端无开关（1.20.5+ 才新增 `shouldAuthenticate=false`，
这正是同类加密方案只支持 1.20.5+ 的原因）。

本 Mod 的做法：检测到当前启动会话不具备正版访问令牌时，跳过这次注定失败的 Mojang 验证，
让加密握手正常完成。

| 玩家类型 | 是否需要安装 |
|----------|--------------|
| 正版玩家 | 装不装都能进（行为不变） |
| 离线玩家 | **必须安装** |
| 其他在线模式服务器 | 无影响（离线令牌本来就过不了验证） |

身份验证仍由登录认证插件（如 CatSeedLogin）负责，本 Mod 不验证身份。

## 安装

放入客户端 `mods` 文件夹（1.20.1 Forge），与服务端 SecureChannel 版本配套：
服务端 1.0.8 ↔ 客户端 1.0.4。

下载：[Releases](https://github.com/lcxws/SecureChannelClient/releases)

## 从源码构建

环境要求：JDK 17、Maven 3.8+。

构建依赖两个第三方库，**因许可原因不随仓库分发**，请手动放入 `tools/` 目录：

| 文件 | 来源 |
|------|------|
| `tools/mixin-0.8.5.jar` | [SpongePowered Maven](https://repo.spongepowered.org/maven/org/spongepowered/mixin/0.8.5/mixin-0.8.5.jar) |
| `tools/authlib-4.0.43.jar` | [Mojang Libraries](https://libraries.minecraft.net/com/mojang/authlib/4.0.43/authlib-4.0.43.jar) |

```bash
mvn package
# 产物: target/SecureChannelClient-1.0.4.jar
```

`settings.xml` 为可选的国内镜像加速配置。

### 关于 `net.minecraft.*` / `net.minecraftforge.*` 源码

`src/main/java` 下这两个包内的类是**编译桩（stub）**：仅为了让 `@Mixin` 注解在编译期
通过类型引用，只包含空方法签名（共 20 余行），**不包含任何反编译的原版/Forge 代码**，
也不会打入最终 jar（运行时由游戏本体提供真实类）。

## 开源协议

[MIT](LICENSE) © 2026 lcxws

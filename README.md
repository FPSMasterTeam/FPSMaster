<div align="center">
<p>
    <img width="200" src="/pictures/logo.png">
</p>

[官方网站](https://fpsmaster.top) |
[BiliBili](https://space.bilibili.com/628246693)
</div>

FPSMaster 是一个免费、强大的 Minecraft PvP 客户端。

## 开源许可证
本项目采用 GPL-3.0 许可证。详情请参阅 [LICENSE](LICENSE) 文件。

特别声明：由于疏忽，自`c6a5edaac43fdcca8ce487eee430e9fb059a2db1`前所有版本的代码均错误地使用了MIT协议开源，现已更正为GPL-3.0。

## 开发环境配置

### MCP
鉴于对Mojang公司代码著作权的保护，我们无法公开相关代码。如果您需要获得相关技术支持，请联系我们。

### Forge
1. clone项目
2. Link Gradle Script
3. 将Idea的Gradle jdk版本设置为java17
4. 导入各版本gradle配置文件
5. 直接使用IDEA打开项目应该会自动识别到启动配置，如果没有，可以手动配置。

`注意：1.8.9和1.12.2应该使用java8运行`


#### 手动配置：

- 1.8.9:

主类：`net.fabricmc.devlaunchinjector.Main`

jvm参数：`-Dfabric.dli.config=/v1.8.9/.gradle/loom-cache/launch.cfg -Dfabric.dli.env=client -Dfabric.dli.main=net.minecraft.launchwrapper.Launch`

- 1.12.2

主类：`net.fabricmc.devlaunchinjector.Main`

jvm参数：`-Dfabric.dli.config=/v1.12.2/.gradle/loom-cache/launch.cfg -Dfabric.dli.env=client -Dfabric.dli.main=net.minecraft.launchwrapper.Launch`

![Alt](https://repobeats.axiom.co/api/embed/e686f6313e4406de4286bf27e0db4a2bf5a31b7f.svg "Repobeats analytics image")

## 引用的开源项目：
[eventbus](https://github.com/therealbush/eventbus)
[patcher](https://github.com/Sk1erLLC/Patcher)

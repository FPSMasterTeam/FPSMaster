<div align="center">
<p>
    <img width="200" src="/pictures/logo.png">
</p>

[官方网站](https://fpsmaster.top) |
[Forum](https://forums.fpsmaster.top) |
[BiliBili](https://space.bilibili.com/628246693)
</div>

FPSMaster 是一个免费、强大的 Minecraft PvP 客户端。

## 开源许可证
本项目采用 MIT 许可证。详情请参阅 [LICENSE](LICENSE) 文件。

- **允许使用**：MIT 许可证允许任何人自由使用、复制、修改、合并、发布、分发、授权和/或销售本软件。
- **保留许可和版权声明**：在所有副本或重要部分中需要包含原始许可和版权声明。
- **不提供担保**：软件是按“原样”提供，没有任何形式的保证或条件。

## 如何配置本项目？

### Forge
1. clone项目
2. Link Gradle Script
3. 将Idea的Gradle jdk版本设置为java17，将项目的jdk版本设置为java8
4. 运行`gradlew clean build`

### MCP
鉴于对Mojang公司代码著作权的保护，我们无法公开相关代码。如果您需要获得相关技术支持，请联系我们。

## 开发环境配置

### Forge
主类：`net.fabricmc.devlaunchinjector.Main`

jvm参数：

1.8.9:

`-Dfabric.dli.config=/v1.8.9/.gradle/loom-cache/launch.cfg -Dfabric.dli.env=client -Dfabric.dli.main=net.minecraft.launchwrapper.Launch`

1.12.2

`-Dfabric.dli.config=/v1.12.2/.gradle/loom-cache/launch.cfg -Dfabric.dli.env=client -Dfabric.dli.main=net.minecraft.launchwrapper.Launch`
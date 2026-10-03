# IMBlocker Forge 1.12.2

这是 IMBlocker 的 Forge 1.12.2 独立兼容分支。

> **测试版本：** 这是 1.12.2 版本的首个测试版本，可能存在未发现的 Bug。请在使用前备份游戏实例，并自行承担测试风险。

## 构建

本分支仅包含 Forge 1.12.2 工程，使用 Java 8 和 ForgeGradle 3：

```text
gradlew.bat clean build --no-daemon --console plain
```

构建环境：

- Minecraft 1.12.2
- Forge 14.23.5.2860
- Java 8

## 功能

- 通过 LaunchWrapper CoreMod 加载输入法控制逻辑。
- 监听 1.12.2 `GuiTextField` 文本框焦点变化。
- 支持聊天框、命令输入、创造模式搜索、铁砧、服务器列表、书本、告示牌和命令方块等原版界面。
- 使用 Windows IMM32 控制输入法状态，并处理输入法上下文恢复和窗口句柄变化。
- 同步现代版本中的输入法转换状态处理逻辑。
- 同步原生候选框位置，使候选窗口靠近当前文本框。
- 命令输入场景自动切换英文输入状态。
- 提供 `DISABLE_IM` 兼容模式，处理不响应转换状态 API 的第三方输入法。

## 输入法兼容性

Windows 后端按照 IMM32 接口设计，目标兼容：

- 微软拼音
- 搜狗输入法
- 讯飞输入法
- QQ 拼音
- 百度输入法
- Rime / 小狼毫
- 其他基于 Windows IMM32 的输入法

默认使用 `CONVERSION_STATUS` 模式。如果第三方输入法不响应转换状态切换，可以在配置文件中设置：

```text
englishStateMode=DISABLE_IM
```

本模组为客户端专用，不需要安装在独立服务器上。

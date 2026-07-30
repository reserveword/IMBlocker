# v7.2.0
此版本兼容 26.x 正式版以及最新快照版本
## 新功能
 - 适配 BlazeSDL 模组以及原版最新快照的 SDL 后端（[#154](https://github.com/reserveword/IMBlocker/issues/154)）
 - 原生兼容 [LDLib2](https://github.com/Low-Drag-MC/LDLib2) 模组
 - 增加 SuperMartijn642's Core Lib 的兼容层
 - 增加 Linux 英文状态命令配置，若正确设置可将 `CONVERSION_STATUS` 作为英文状态实现方式（参见 [#150](https://github.com/reserveword/IMBlocker/issues/150)）
 - 增加命令前缀正则表达式配置，可以加入“/”以外的命令前缀
## 修复与改进
 - 额外缩放配置项现在开放给所有平台的 GLFW 后端
 - 兼容新版本的 Reese's Sodium Options 模组并**不再兼容旧版本**
 - 增加按键翻译补丁解决非 Windows 平台在 `DISABLE_IM` 英文状态实现方式下由于 `SDL_StopTextInput` 无法输入字符的问题（[#154](https://github.com/reserveword/IMBlocker/issues/154)，[#131](https://github.com/reserveword/IMBlocker/issues/131)）
## 杂项
 - Windows 平台的游戏内输入法将**始终保留为可选项**（需要通过 Cloth Config 打开）
 
[历史更新日志](https://github.com/reserveword/IMBlocker/blob/26.1%2B/Changelog_History.md)
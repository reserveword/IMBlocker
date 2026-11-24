 - 开始广泛兼容使用 ImGui 制作界面的模组，当前已支持 FlashBack 模组
 - 与操作系统绑定的配置项现在仅在对应平台上可见
 - 通过 PolyCompose 的文本输入会话挂钩 OneConfig 等使用 Compose 文本框的模组输入法焦点，仅在实际文本框获得焦点时启用输入法；并从会话请求中读取焦点文本框与光标的实时矩形，使候选词窗口精确跟随光标（已在 PolyCompose 3.0.7 上测试）
 - 修复屏幕切换后输入法状态残留的问题：在 Compose 界面（如 OneConfig）中，只有当文本框真正通过会话获得焦点时才会启用输入法，避免此前界面残留的输入法状态使候选框出现在屏幕中央
 - 加强 Compose 会话挂钩的诊断：会话捕获失效或缺少布局数据时会输出明确的警告日志
 - 修复 Compose 界面被覆盖或隐藏后重新显示时，会话未重新捕获导致候选框回退到屏幕中央的问题
 
**注意**：在 NeoForge 26.3 版本中无法显示输入法候选框的 Windows 用户请在配置中启用**经典合成字符样式**或**游戏内输入法**选项（详见[NeoForge#3447](https://github.com/neoforged/NeoForge/issues/3447#issuecomment-6722928418)）
 
[历史更新日志](https://github.com/reserveword/IMBlocker/blob/26.1%2B/Changelog_History.md)
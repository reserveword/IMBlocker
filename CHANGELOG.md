# v5.6.0
## 新功能
 - Linux (X11)：使用原生系统函数控制输入法状态，不再需要配置命令
 - Linux (X11)：已实现候选框跟踪文本光标
 - macOS：已实现候选框跟踪文本光标
 - macOS：现在预编辑字符可以在游戏内显示
 - 为中州韵输入法新增 `RIME` 英文状态实现方式，注意事项：
   - Windows：需要将输入法安装目录添加至 `PATH`
   - macOS：需要从 Github 安装最新的 nightly build
   - Linux：仅支持 fcitx5-rime，**ibus-rime 不受支持**
 - 原生兼容 LDLib2 模组
 - 增加命令前缀正则表达式配置，可以加入“/”以外的命令前缀
## 修复与改进
 - Windows：修复初始焦点状态可能不正确的问题
 - macOS：现在 `DISABLE_IM` 英文状态实现方式可正常输入字符
 - 完全修复部分平台高 DPI 缩放下候选框坐标不正确的问题
 - 略微提升文本光标位置的计算精度
 - 模拟字符定位焦点现在以黑名单形式与屏幕关联
## 杂项
 - Reese's Sodium Options 的支持版本调整为 2.2.0+

[历史更新日志](https://github.com/reserveword/IMBlocker/blob/master/Changelog_History.md)

---
**至此，本模组所有计划内功能已全部实现，正式进入长期维护阶段**
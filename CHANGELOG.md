# v7.3.0
## 新功能
 - Linux (X11)：使用原生系统函数控制输入法状态，不再需要配置命令
 - 为中州韵输入法新增 `RIME` 英文状态实现方式，注意事项：
   - Windows：需要将输入法安装目录添加至 `PATH`
   - macOS：需要从 Github 安装最新的 nightly build
   - Linux：仅支持 fcitx5-rime，**ibus-rime 不受支持**
## 修复与改进
 - Windows：修复初始焦点状态可能不正确的问题
 - macOS：现在 `DISABLE_IM` 英文状态实现方式可正常输入字符
 - LDLib2：计算文本光标位置时考虑矩阵变换产生的偏移
 - 完全修复部分平台高 DPI 缩放下候选框坐标不正确的问题，移除额外缩放配置
 - 略微提升文本光标位置的计算精度
 - 模拟字符定位焦点现在以黑名单形式与屏幕关联
## 杂项
 - Reese's Sodium Options 的支持版本调整为 2.2.0+
 
[历史更新日志](https://github.com/reserveword/IMBlocker/blob/26.1%2B/Changelog_History.md)
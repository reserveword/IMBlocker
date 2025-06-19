# v5.3.0 ([#102](https://github.com/reserveword/IMBlocker/pull/102))
**⚠注意⚠：此版本更新内容较多，请仔细阅读更新日志**
## 修复与改进
**从此版本开始，所有平台统一使用 Cloth Config 管理配置项，如需更改默认配置，请安装它！**  
修复 ModMenu 以外的模组菜单无法打开配置界面的问题（[#96](https://github.com/reserveword/IMBlocker/issues/96)）  
修复 Minecraft 1.21.6 启动游戏崩溃的问题（[#100](https://github.com/reserveword/IMBlocker/issues/100)）  
添加 Windows 兼容性设置配置选项，帮助解决部分输入法的兼容问题（[#98](https://github.com/reserveword/IMBlocker/issues/98)）  
Windows：文本光标超出文本框边界时候选词框将定位至距其最近的文本框边界  
白名单屏幕匹配机制现在为`instanceof`（**此配置项可能需要重新编辑，详见[下文](#白名单调整说明)**）  
改进了 Windows 全屏模式下的窗口属性，使其能还原到正确的窗口状态  
更新模组 Logo（by [@Halogly](https://github.com/Halogly)，[#101](https://github.com/reserveword/IMBlocker/issues/101)）
### 白名单调整说明
从先前的版本更新至此版本后，屏幕白名单配置项需要作出一定调整。为降低操作门槛，这里直接给出具体调整步骤，**在完成第三个步骤前先不要更新模组版本**：
- 首先记录下当前的屏幕白名单为列表 A
- 然后**重置**屏幕白名单并记录为列表 B（此时即为老版默认白名单）
- 将 A 中同时属于 B 的元素除去（即集合操作A-B）得到列表 C
- 更新模组版本后再次重置屏幕白名单并添加列表 C 中的内容至白名单
## 新功能
新增原生兼容模组：BlockUI、SuperMartijn642's Core Lib  
Windows：合成字符串的字体大小现在可以自动根据界面尺寸调整
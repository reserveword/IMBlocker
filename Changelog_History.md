# 5.4.2
 - 增加首选中英文状态配置项，用于指定文本框获得焦点后输入法的初始中英文状态

<br>

# 5.4.1.1
 - 在游戏主类访问接口的类加载阶段创建实例以避免空指针访问（[#109](https://github.com/reserveword/IMBlocker/issues/109)）

<br>

# 5.4.1
 - 修复与 Ixeris 一起使用时本模组功能不生效的问题
 - 修复 5.4.0.x 与 Easy Anvils 的微小兼容问题
 - 原生兼容 Notes（笔记）模组

<br>

# 5.4.0.1
 - 修复 Fabric 游戏版本检测方法中漏掉的[等号](https://github.com/LitnhJacuzzi/IMBlocker/compare/95fdfc0..97349f3#diff-9e06d162918f68b78952fbb0bff96702d32c6701944743ffcad7edddfedbe301R19)

<br>

# 5.4.0
**此版本包含重要的技术和功能更新**
## 修复与改进
 - 在类加载阶段创建默认配置实例以避免空指针访问（[#105](https://github.com/reserveword/IMBlocker/issues/105)）
 - (Neo)Forge 现在包含所有已兼容模组的注入类以支持信雅互联
 - 修复 Axiom 编辑器打开时输入法候选框位置未随游戏内容偏移的问题
 - 修复屏幕白名单配置项编辑后需要重启游戏才生效的问题
## 新功能
 - 使用更严格的可见性检测和候选队列机制处理多焦点竞争问题，大幅提升焦点组件的定位精度
 - 增加使用模拟字符定位焦点组件的选项（详细信息见游戏内配置项）
 - **输入法候选框现在可以定位至 Axiom 的文本光标**
##
※本模组核心模块的源代码现在包含详细的文档和注释

<br>

# 5.3.1
 - 为 Forge 添加钠视频界面的兼容  
 - 过滤 JourneyMap 的全屏地图产生的无效焦点请求

<br>

# 5.3.0
**⚠注意⚠：此版本更新内容较多，请仔细阅读更新日志**
## 修复与改进
 - **从此版本开始，所有平台统一使用 Cloth Config 管理配置项，如需更改默认配置，请安装它！**  
 - 修复 ModMenu 以外的模组菜单无法打开配置界面的问题（[#96](https://github.com/reserveword/IMBlocker/issues/96)）  
 - 修复 Minecraft 1.21.6 启动游戏崩溃的问题（[#100](https://github.com/reserveword/IMBlocker/issues/100)）  
 - 添加 Windows 兼容性设置配置选项，帮助解决部分输入法的兼容问题（[#98](https://github.com/reserveword/IMBlocker/issues/98)）  
 - Windows：文本光标超出文本框边界时候选词框将定位至距其最近的文本框边界  
 - 白名单屏幕匹配机制现在为`instanceof`（**此配置项可能需要重新编辑，详见[下文](#白名单调整说明)**）  
 - 改进了 Windows 全屏模式下的窗口属性，使其能还原到正确的窗口状态  
 - 更新模组 Logo（by [@Halogly](https://github.com/Halogly)，[#101](https://github.com/reserveword/IMBlocker/issues/101)）
### 白名单调整说明
从先前的版本更新至此版本后，屏幕白名单配置项需要作出一定调整。为降低操作门槛，这里直接给出具体调整步骤，**在完成第三个步骤前先不要更新模组版本**：
- 首先记录下当前的屏幕白名单为列表 A
- 然后**重置**屏幕白名单并记录为列表 B（此时即为老版默认白名单）
- 将 A 中同时属于 B 的元素除去（即集合操作A-B）得到列表 C
- 更新模组版本后再次重置屏幕白名单并添加列表 C 中的内容至白名单
## 新功能
 - 新增原生兼容模组：BlockUI、SuperMartijn642's Core Lib  
 - Windows：合成字符串的字体大小现在可以自动根据界面尺寸调整

<br>

# 5.2.2
 - 修复原版文本框小概率出现的无效文本光标索引导致的崩溃（[#93](https://github.com/reserveword/IMBlocker/issues/93)）  
 - 增强对多行文本框的支持（原版/FTB Library）

<br>

# 5.2.1
 - NeoForge：修复屏幕记录功能失效的问题  
 - Fabric：支持 [LibGui](https://github.com/CottonMC/LibGui)  
 - Fabric/NeoForge：完全适配 Reese's Sodium Options  

现在输入法候选框可实时追踪除 Axiom 外所有已兼容模组的文本光标（Axiom 组件非 Java 实现注入成本较大）

<br>

# 5.2.0
 - 大幅增加版本兼容范围（包括兼容的模组）：  
Fabric -> 1.17+  
Forge -> 1.16.5, 1.17~1.20.4

<br>

# 5.1.2
## 修复与改进
 - 修复调用FTB内部组件方法可能导致的空指针异常（[#88](https://github.com/reserveword/IMBlocker/issues/88)）  
 - 防止模组内部系统方法与被注入类的方法重名，这可能会解决一些潜在的冲突  
## 新功能
 - 输入法候选框现在可以实时跟踪 Meteor Client 的文本光标

<br>

# v5.1.1
 - Windows：增强输入法候选框的定位功能

<br>

# v5.1.0
## 修复与改进
 - 使用标准焦点管理系统完全重写底层架构，修复所有主要漏洞  
 - 移除新架构下不再需要的配置项目  
 - 增强版本兼容范围，分离不同的模组加载器  
 - 增加对部分 forge 模组的支持  
 - 修正默认白名单屏幕列表（可能需要重置该列表以生效）
 - 命令方块界面的命令输入框默认中英文状态现在为英文
## 新增功能
 - Windows：输入法候选框现在能自动定位至文本框  
 - Windows：输入法候选框现在能在全屏模式下显示  
 - 为无法在检测到聊天栏命令格式时自动切换中英文状态的输入法提供替代方案（新配置项） 
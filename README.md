# IMBlocker: 智能屏蔽输入法

[English version](https://github.com/reserveword/IMBlocker/blob/master/README.en.md)

## 简介

IMBlocker 是一款 [Minecraft](https://minecraft.net/) 模组，能够在游戏中自动切换输入法开关状态。

在 Minecraft 游戏过程中，我们不得不关闭输入法以进行各种游戏操作，又不得不开启输入法以聊天/搜索。频繁切换输入法十分麻烦。

IMBlocker 模组能够识别当前游戏状态，并自动启用/禁用输入法。

## 功能

- 自动根据当前输入上下文调整输入法状态
- 监测聊天栏文本是否为命令格式并自动切换中英文状态
- 修复输入法候选框无法定位至文本框的问题（仅Windows）
- 令输入法候选框可以在全屏模式下显示（仅Windows）

## 原理

- 决定输入法状态的根本因素
    - 在一个标准的图形界面框架中，有且仅有一个组件会接收并响应非全局键盘事件，它被称为**焦点组件**
    - 当持有焦点时，一部分组件希望接收到文本字符（如各种文本框），另一部分组件则希望接收到原始键盘输入（如游戏操作界面、按钮、列表等），它们的首选按键接收类型决定了输入法的首选状态
    - 在 Minecraft 的界面实现中，虽然没有统一的焦点管理机制，但总会仅存在一个组件可以接收并响应键盘输入，我们称之为**等效焦点组件**
    - 在 Minecraft 中，并非所有组件的焦点变化请求都是有效的，本模组会根据情况过滤无效请求
- IMBlocker 的输入状态识别
    - 通过 Mixin 注入的方式为已知组件的焦点状态变化添加回调机制
    - 构建焦点管理系统，将各种组件焦点状态的变化按照标准焦点管理规范将实际焦点变化结果映射到全局焦点路径中，并据此调整输入法状态
- 例外情况
    - 有些 GUI 屏幕（比如书与笔和告示牌）显示时直接处理输入字符而不产生焦点请求
    - 我们将这些屏幕（Screen）视为等效焦点组件并放入白名单，显示与关闭时显式产生焦点变化请求。如果你需要开启输入法的地方看起来像一个屏幕而非文本框类组件，请尝试使用屏幕记录功能识别并将其添加到白名单
- 问题
    - 未被注入的文本框组件无法被焦点管理系统监听，若某模组拥有自己的组件框架实现本模组可能无法自动为其调整输入法状态，此时请将它们提交到 [#13](https://github.com/reserveword/IMBlocker/issues/13) 供开发者处理
    - GLFW 窗口系统在 Windows 下有一个已知漏洞：如果游戏窗口被创建时未获得焦点则第一次获得焦点不会触发焦点回调，此时可能出现锁键现象。解决方法：使游戏窗口失去再获得一次焦点
    - 欢迎提出建议或反馈

## 已支持的具有自定义GUI实现的模组

[Roughly Enough Items](https://github.com/shedaniel/RoughlyEnoughItems)  
[EMI](https://github.com/emilyploszaj/emi)  
[Axiom](https://axiom.moulberry.com/)  
[Replay Mod](https://www.replaymod.com/)  
[FTB Library](https://github.com/FTBTeam/FTB-Library)  
[Meteor Client](https://www.meteorclient.com/)  
[LibGui](https://github.com/CottonMC/LibGui)  
[Reese's Sodium Options](https://github.com/FlashyReese/reeses-sodium-options)  
[BlockUI](https://github.com/ldtteam/BlockUI)  
[SuperMartijn642's Core Lib](https://github.com/SuperMartijn642/SuperMartijn642sCoreLib)

## 致谢

感谢 [TimmyOVO](https://www.mcbbs.net/?1696224) 制作的 [InputMethodBlocker](https://www.mcbbs.net/thread-688825-12-1.html) ，启发我制作这个mod  
注意，原贴中代码地址已失效，目前 Github 上的仓库为 [InputMethodBlocker](https://github.com/lss233/InputMethodBlocker)  

模组 Logo by [@Halogly](https://github.com/Halogly)
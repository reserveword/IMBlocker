# IMBlocker: 智能屏蔽输入法

[English version](https://github.com/reserveword/IMBlocker/blob/master/README.en.md)

## 简介

IMBlocker 是一款 [Minecraft](https://minecraft.net/) 模组，能够在游戏中自动切换输入法开关状态。

在 Minecraft 游戏过程中，我们不得不关闭输入法以进行各种游戏操作，又不得不开启输入法以聊天/搜索。频繁切换输入法十分麻烦。

IMBlocker 模组能够识别当前游戏状态，并自动启用/禁用输入法。

## 原理

- Minecraft 的输入处理机制
    - 在 Minecraft 游戏中，一次键盘输入会先后处理游戏按键响应和文字输入响应
    - 游戏按键响应处理与我们无关，因此忽略
    - 文字输入响应会交由 Minecraft 当前显示的 GUI 屏幕处理，屏幕一般会转而调用某个文本框来处理输入字符
    - 如果不存在屏幕，或者屏幕判断不该调用文本框，或者文本框拒绝接收这一字符，那么文字不会被“输入”到任何地方
    - 无论文字是否会被输入，文字输入响应过程都会执行
    - 我们几乎无法事先确定游戏会不会接受我们输入文字
- IMBlocker 的输入状态识别
    - 我们收集所有文本框对象
    - 使用ASM修改文本框的构造函数以实现这一点
    - 如果有文本框允许输入，则认为游戏接受我们输入文字
- 例外情况
    - 有些GUI屏幕（比如书与笔和告示牌）直接处理输入字符，而不是转移给文本框来处理
    - 我们无法捕捉这些输入，因此我们将这些屏幕（Screen）放入白名单
- 问题
    - 有可能出现假阳性，也就是文本框认为自己能接受输入，但游戏不会把输入传给这个文本框
    - 非Windows环境下没有可用的方法禁用/启用输入法
    - 欢迎提出建议或反馈
## 致谢

感谢 [TimmyOVO](https://www.mcbbs.net/?1696224) 制作的 [InputMethodBlocker](https://www.mcbbs.net/thread-688825-12-1.html) ，启发我制作这个mod

注意，原贴中代码地址已失效，目前 Github 上的仓库为 [InputMethodBlocker](https://github.com/lss233/InputMethodBlocker)
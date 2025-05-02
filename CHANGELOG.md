## 修复与改进
修复调用FTB内部组件方法可能导致的空指针异常（[#88](https://github.com/reserveword/IMBlocker/issues/88)）
防止模组内部系统方法与被注入类的方法重名，这可能会解决一些潜在的冲突
## 新功能
输入法候选框现在可以实时跟踪Meteor Client的文本光标
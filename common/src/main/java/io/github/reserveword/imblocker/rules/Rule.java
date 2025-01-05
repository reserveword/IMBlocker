package io.github.reserveword.imblocker.rules;

public interface Rule {
    /**
     * 返回规则优先级，对每条规则每次调用必须返回相同的结果
     *
     * @return 规则优先级，越高越优先
     */
    double Priority();

    /**
     * 执行规则，计算并返回在本条规则下是否应当启用输入法
     *
     * @return 是否结束规则链执行
     */
    boolean apply();
}

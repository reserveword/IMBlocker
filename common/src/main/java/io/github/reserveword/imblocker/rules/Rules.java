package io.github.reserveword.imblocker.rules;

import io.github.reserveword.imblocker.common.IMManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Rules {
    private static final ArrayList<Rule> rules = new ArrayList<>();

    static {
        register(new ChatRule(), new FocusRule(), new ScreenListRule());
    }

    /**
     * 注册规则
     * 注意：尽量把所有规则一次性注册完毕
     *
     * @param rule 待注册规则
     */
    public static void register(Rule ...rule) {
        rules.addAll(Arrays.asList(rule));
        rules.sort(Comparator.comparingDouble(Rule::Priority).reversed());
    }

    public static void apply() {
        for (Rule rule: rules) {
            if (rule.apply()) return;
        }
        IMManager.setState(false);
    }
}

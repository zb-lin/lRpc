package com.lzb.flowcontrol.sentinel;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.lzb.config.RpcConfig;
import com.lzb.enums.FailureStrategyEnum;
import com.lzb.flowcontrol.FlowControl;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.alibaba.csp.sentinel.slots.block.RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER;

@Slf4j
public class SentinelFlowControl implements FlowControl {
    public static final Set<String> ruleSet = new CopyOnWriteArraySet<>();
    private final RpcConfig rpcConfig = RpcConfig.getRpcConfig();

    private void initFlowRules(String key) {
        if (ruleSet.contains(key)) {
            return;
        }
        ruleSet.add(key);
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource(key);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(Double.parseDouble(rpcConfig.getQps()));
        if (FailureStrategyEnum.WAIT.getName().equals(rpcConfig.getFailureStrategy())) {
            rule.setControlBehavior(CONTROL_BEHAVIOR_RATE_LIMITER);
        }
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }

    @Override
    public void doFlowControlWithWait(String key) {
        initFlowRules(key);
        try (Entry entry = SphU.entry(key)) {
            log.info("pass through [{}]", key);
        } catch (BlockException e) {
            log.error("The service is restricted in flow: [{}]", key, e);
        }
    }

    @Override
    public boolean doFlowControlWithFailFast(String key) {
        initFlowRules(key);
        try (Entry entry = SphU.entry(key)) {
            return true;
        } catch (BlockException e) {
            return false;
        }
    }
}

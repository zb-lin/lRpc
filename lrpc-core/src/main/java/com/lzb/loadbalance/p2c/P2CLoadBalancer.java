package com.lzb.loadbalance.p2c;

import com.lzb.loadbalance.AbstractLoadBalance;
import com.lzb.remoting.dto.RpcRequest;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * P2C 算法
 */
public class P2CLoadBalancer extends AbstractLoadBalance {

    /**
     * 闲置时间的最大容忍值
     * 单位：纳秒（3s）
     */
    private static final long forceGap = 3000_000_000L;

    private static final Random r = new Random();
    /**
     * 保存了参与lb的节点集合
     */
    private List<Node> nodes;


    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        this.nodes = serviceAddresses.stream().map(serviceAddress -> new Node(serviceAddress, 1)).collect(Collectors.toList());
        return pick(System.currentTimeMillis()).host;
    }

    /**
     * 外界给入start，值为当前时间，resp后应给recycle传同样的值
     */
    public Node pick(long start) {
        Node pc, upc;
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("no node!");
        }
        if (nodes.size() == 1) {
            return nodes.get(0);
        }

        Node[] randomPair = prePick();

        /*
          这里根据各自当前指标，计算出谁更合适被pick
          计算方式：
                 nodeA.load                           nodeB.load
          ----------------------------   :   ----------------------------
          nodeA.health * nodeA.weight        nodeB.health * nodeB.weight

          health和weight都是提权用的，而load是降权用的，所以用load除以heal和weight的乘积，计算出的值越大，越不容易被pick
         */
        if (randomPair[0].load() * randomPair[1].health() * randomPair[1].weight >
                randomPair[1].load() * randomPair[0].health() * randomPair[0].weight) {
            pc = randomPair[1];
            upc = randomPair[0];
        } else {
            pc = randomPair[0];
            upc = randomPair[1];
        }

        // 如果落选的节点，在forceGap期间内没有被选中一次，那么强制选中一次，利用强制的机会，来触发成功率、延迟的衰减
        long pick = upc.pick.get();
        if ((start - pick) > forceGap && upc.pick.compareAndSet(pick, start)) {
            pc = upc; //强制选中
        }

        // 节点未发生切换才更新pick时间
        if (pc != upc) {
            pc.pick.set(start);
        }
        pc.inflight.incrementAndGet();

        return pc;
    }

    /**
     * pick出去后，等来了response后，应触发该方法
     */
    public void recycle(Node node, long pickTime, long cpu, boolean error) {
        node.responseTrigger(pickTime, cpu, error);
    }

    /**
     * 随机选择俩节点
     */
    public Node[] prePick() {
        Node[] randomPair = new Node[2];
        for (int i = 0; i < 3; i++) {
            int a = r.nextInt(nodes.size());
            int b = r.nextInt(nodes.size() - 1);
            if (b >= a) {
                b += 1; //防止随机出的节点相同
            }
            randomPair[0] = nodes.get(a);
            randomPair[1] = nodes.get(b);
            if (randomPair[0].valid() || randomPair[1].valid()) {
                break;
            }
        }
        return randomPair;
    }
}
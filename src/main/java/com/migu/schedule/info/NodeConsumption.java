package com.migu.schedule.info;

/**
 * 服务器任务消耗总数类
 */
public class NodeConsumption {

    private int nodeId;
    private int consumptions;

    public int getNodeId() {
        return nodeId;
    }

    public int getConsumptions() {
        return consumptions;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public void setConsumptions(int consumptions) {
        this.consumptions = consumptions;
    }

    public NodeConsumption(int nodeId, int consumptions) {
        this.nodeId = nodeId;
        this.consumptions = consumptions;
    }
}

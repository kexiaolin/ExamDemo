package com.migu.schedule.info;

import java.util.*;

/**
 * 调度工具类
 */
public class ScheduleUtil {
    /**
     * 获取服务器任务队列中上最小消耗的服务器
     * @param seviceTasks
     * @return
     */
    public static int getLeastConsumptionNode(Map<Node,List<Task>>seviceTasks){

        List<NodeConsumption> consumptions =new ArrayList<NodeConsumption>();//将每个服务器上的消耗率总值存进来
        //遍历服务器任务map
        for (Map.Entry<Node, List<Task>> entry : seviceTasks.entrySet()) {

            Node node =entry.getKey();
            List<Task> taskList=entry.getValue();
            Iterator it=taskList.iterator();

            int sum=0;//单个服务器消耗总值
            while(it.hasNext()){

                Task task=(Task) it.next();
                sum+=task.getConsumption();
            }
            NodeConsumption nodeConsumption=new NodeConsumption(node.getNodeId(),sum);
            consumptions.add(nodeConsumption);

        }
        //排序，找到最小的消耗率的服务器
        Collections.sort(consumptions, new Comparator<NodeConsumption>() {
            public int compare(NodeConsumption o1, NodeConsumption o2) {
                return o1.getConsumptions()-o2.getConsumptions();
            }
        });


        return  consumptions.get(0).getNodeId();
    }

    /**
     * 获取每个服务器上任务的数量
     * @param seviceTasks
     * @return
     */
    public Map<Integer,Integer> getNodeSeviceTaskCount(Map<Node,List<Task>>seviceTasks){

        Map<Integer,Integer> nodeTasksCounts =new HashMap<Integer, Integer>();//将每个服务器上的任务数量存储进来
        //遍历服务器任务map
        for (Map.Entry<Node, List<Task>> entry : seviceTasks.entrySet()) {

            Node node =entry.getKey();
            List<Task> taskList=entry.getValue();
            nodeTasksCounts.put(node.getNodeId(),taskList.size());

        }

        return nodeTasksCounts;
    }
}

package com.migu.schedule;


import com.migu.schedule.constants.ReturnCodeKeys;
import com.migu.schedule.info.Node;
import com.migu.schedule.info.ScheduleUtil;
import com.migu.schedule.info.Task;
import com.migu.schedule.info.TaskInfo;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/*
*类名和方法不能修改
 */
public class Schedule {

    //挂起队列
    private Map<Integer,Task> taskQueue=new HashMap(40);

    //服务器map
    private  Map<Integer,Node> serviceNode=new HashMap<Integer, Node>();

   //每个服务器上的任务
    private  Map<Node,List<Task>> serviceTasks=new HashMap<Node, List<Task>>();

    public int init() {

        serviceNode.clear();
        serviceTasks.clear();
        taskQueue.clear();
        return ReturnCodeKeys.E001;
    }


    public int registerNode(int nodeId) {

        if (nodeId <=0)
             return ReturnCodeKeys.E004;
        if (serviceNode.containsKey(nodeId)){

            return ReturnCodeKeys.E005;
        }
        Node node=new Node(nodeId,"服务器："+nodeId);
        serviceNode.put(nodeId,node);
        return ReturnCodeKeys.E003;
    }

    public int unregisterNode(int nodeId) {

        if (nodeId <=0)
            return ReturnCodeKeys.E004;
        if (!serviceNode.containsKey(nodeId)){

            return ReturnCodeKeys.E007;
        }
        Node node =serviceNode.get(nodeId);

        if (serviceTasks.containsKey(node)){

            List<Task> tasks=serviceTasks.get(node);
            if (!tasks.isEmpty()){//服务器上运行了任务,移到挂起队列中

                 for(Task task:tasks){

                     taskQueue.put(task.getTaskId(),task);
                 }
            }
            //服务器上没有跑任务
            else {
                serviceNode.remove(nodeId);
            }

        }
        else{
            serviceNode.remove(nodeId);
        }
        return ReturnCodeKeys.E006;
    }


    public int addTask(int taskId, int consumption) {

        if (taskId<=0){
            return ReturnCodeKeys.E009;
        }
        if (taskQueue.containsKey(taskId)){

            return ReturnCodeKeys.E010;
        }
        Task task=new Task(taskId,consumption);
        taskQueue.put(taskId,task);
        return ReturnCodeKeys.E008;
    }


    public int deleteTask(final int taskId) {
        if (taskId<=0){
            return ReturnCodeKeys.E009;
        }
        if (!taskQueue.containsKey(taskId)){

            return ReturnCodeKeys.E012;
        }
        taskQueue.remove(taskId);//挂起队列中的任务删除
        //运行中的任务删除
        for (Map.Entry<Node, List<Task>> entry : serviceTasks.entrySet()) {

             Node node =entry.getKey();
             List<Task> tasks=entry.getValue();
            Iterator it=tasks.iterator();
             while(it.hasNext()){
                 int tid=(Integer) it.next();
                 if (tid==taskId){//任务存在删除
                     it.remove();
                 }
            }

        }
        return ReturnCodeKeys.E011;
    }


    public int scheduleTask(int threshold) {

        if (threshold<=0){
            return ReturnCodeKeys.E002;
        }
        if (!taskQueue.isEmpty()){//挂起队列中还有任务时，分配服务器运行
            for (Map.Entry<Integer, Task> entry : taskQueue.entrySet()) {


                 int nodeId= ScheduleUtil.getLeastConsumptionNode(serviceTasks);//找到最小消耗率的服务器id

                 if (nodeId==-1){//服务器上暂时没有任务

                     for(Map.Entry<Integer,Node> ent:serviceNode.entrySet()){

                         Node node=(Node)ent.getValue();
                         List<Task> tks=new ArrayList<Task>();
                         tks.add(entry.getValue());
                         serviceTasks.put(node,tks);
                         break;
                     }

                 }
                 Node node=new Node(nodeId,"服务器："+nodeId);
                 List<Task> tasks=serviceTasks.get(node);//当前服务器上运行的task
                 tasks.add(entry.getValue());//添加task到该服务器的tasks里面
                 serviceTasks.put(node,tasks);//更新服务器任务列表


            }

        }
        return ReturnCodeKeys.E013;
    }


    public int queryTaskStatus(List<TaskInfo> tasks) {
        if (tasks ==null){
            return ReturnCodeKeys.E016;
        }
        //遍历服务器任务map
        for (Map.Entry<Node, List<Task>> entry : serviceTasks.entrySet()) {

            Node node =entry.getKey();
            List<Task> taskList=entry.getValue();
            Iterator it=taskList.iterator();
            while(it.hasNext()){

                   Task task=(Task) it.next();
                   TaskInfo taskInfo=new TaskInfo();
                   taskInfo.setTaskId(task.getTaskId());
                   taskInfo.setNodeId(node.getNodeId());
                   //将服务器上的任务添加到tasks
                   tasks.add(taskInfo);
            }

        }
        return ReturnCodeKeys.E015;
    }

}

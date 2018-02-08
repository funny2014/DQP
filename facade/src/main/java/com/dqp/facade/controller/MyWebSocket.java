package com.dqp.facade.controller;

import com.dqp.facade.rpc.ExcutorFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  使用websocket长连接技术实现日志的打印功能
 */
@ServerEndpoint("/websocket/{jobId}")
@Component
public class MyWebSocket {

    private static int onlineCount = 0;

    // public static CopyOnWriteMap<String,MyWebSocket> webSocketSet = new CopyOnWriteMap<>();
    public static Map<String,MyWebSocket> webSocketSet = new HashMap<>();

    private Session session;

    private String jobID;


    private static ExcutorFeignClient excutorFeignClient;
    @Autowired
    public void setExcutorFeignClients(ExcutorFeignClient excutorFeignClient){
        MyWebSocket.excutorFeignClient = excutorFeignClient;
    }

    /**
     * Get online count int.
     */
    public static synchronized int getOnlineCount() {
        return MyWebSocket.onlineCount;
    }

    /**
     * Add online count.
     */
    public static synchronized void addOnlineCount() {
        MyWebSocket.onlineCount++;
    }

    /**
     * Sub online count.
     */
    public static synchronized void subOnlineCount() {
        MyWebSocket.onlineCount--;
    }

    @OnOpen
    public void onOpen(@PathParam("jobId")  String jobId, Session session)throws IOException{
        this.session = session;
        this.jobID = jobId;
        webSocketSet.put(jobId,this);
        addOnlineCount();
        System.out.println("有新链接加入!当前在线人数为" + getOnlineCount());
    }



    @OnClose
    public void onClose() {
        webSocketSet.remove(this.jobID);
        subOnlineCount();
        System.out.println("有一链接关闭!当前在线人数为" + getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("来自客户端的消息SQL:" + message);
        List<String> logList;
        try {
            while (true) {
                logList = excutorFeignClient.getSqlLog(jobID);
                if (logList.size() != 0) {
                    //session id 是唯一的标识符
                    for (String log : logList) {
                        this.sendMessage("oo:" + log + "\n");
                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 群发消息
//        for ( MyWebSocket item : webSocketSet ){
//                item.sendMessage(message + "hahahaahah");
//        }
    }

    /**
     * Send message.
     *
     * @param message the message
     * @throws IOException the io exception
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}


@Slf4j
class getLog implements Runnable {
    private String jobID;
    private Session session ;

    private ExcutorFeignClient excutorFeignClient;

    public getLog(String jobID,ExcutorFeignClient excutorFeignClient,Session session){
        this.jobID = jobID;
        this.excutorFeignClient = excutorFeignClient;
        this.session = session;
    }

    @Override
    public void run() {
        try {
            List<String> logList = excutorFeignClient.getSqlLog(jobID);
            System.out.println("ws==="+logList.size());
            if (logList != null && logList.size() != 0) {
                for (String log : logList) {
                    session.getBasicRemote().sendText(log + "\n");
                    //ws.sendMessage(log + "\n");
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
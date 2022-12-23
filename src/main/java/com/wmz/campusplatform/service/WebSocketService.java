package com.wmz.campusplatform.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.wmz.campusplatform.details.ConversationDetails;
import com.wmz.campusplatform.pojo.SocketMsg;
import okhttp3.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.WebSocketClient;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @description websocket的具体实现类
 *  使用springboot的唯一区别是要@Component声明，而使用独立容器是由容器自己管理websocket的，
 *  但在springboot中连容器都是spring管理的。
 *  虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，
 *  所以可以用一个静态set保存起来
 */
@ServerEndpoint(value = "/websocket/{stuId}")
@Component
public class WebSocketService {
    private String stuId;
    private Session session;

    //用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketService> webSocketSet = new CopyOnWriteArraySet<WebSocketService>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    //用来记录sessionId和该session进行绑定
    private static Map<String, Session> map = new HashMap<String, Session>();

    private static ChatService chatService;

    @Autowired
    public void setChatService(ChatService chatService){
        WebSocketService.chatService = chatService;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("stuId") String stuId) {
        Map<String,Object> message=new HashMap<String, Object>();
        this.session = session;
        this.stuId = stuId;
        map.put(stuId, session);
        webSocketSet.add(this);//加入set中
        message.put("type",0); //消息类型，0-getConversation，1-getMessage and getConversation
        List<ConversationDetails> myConversation = chatService.getMyConversation(stuId);
        message.put("myConversation", myConversation);
        this.session.getAsyncRemote().sendText(new Gson().toJson(message));
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this); //从set中删除
        System.out.println("有一连接关闭！当前在线人数为" + webSocketSet.size());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("stuId") String stuId) {
        System.out.println("来自客户端的消息-->" + stuId + ": " + message);

        //从客户端传过来的数据是json数据，所以这里使用jackson进行转换为SocketMsg对象，
        // 然后通过socketMsg的type进行判断是单聊还是群聊，进行相应的处理:
        ObjectMapper objectMapper = new ObjectMapper();
        SocketMsg socketMsg;

        try {
            socketMsg = objectMapper.readValue(message, SocketMsg.class);
            //save message
            chatService.saveMessage(socketMsg.getToUser(), socketMsg.getFromUser(), socketMsg.getMsg(), socketMsg.getIsFile(), socketMsg.getTempFilePath(), socketMsg.getSuffixName(), socketMsg.getFileName());
            //get sender messageList and conversationList
            Map<String,Object> senderMessage = new HashMap<String, Object>();
            senderMessage.put("type", 1);
            senderMessage.put("myConversation", chatService.getMyConversation(socketMsg.getFromUser()));
            senderMessage.put("myMessage", chatService.getMessageList(socketMsg.getFromUser(), socketMsg.getToUser()));
            //check conversation
            senderMessage.put("toUser", socketMsg.getToUser());
            //get receiver messageList and conversationList
            Map<String,Object> receiverMessage = new HashMap<String, Object>();
            receiverMessage.put("type", 1);
            receiverMessage.put("myConversation", chatService.getMyConversation(socketMsg.getToUser()));
            receiverMessage.put("myMessage", chatService.getMessageList(socketMsg.getToUser(), socketMsg.getFromUser()));
            //check conversation
            receiverMessage.put("toUser", socketMsg.getFromUser());
            //get session
            Session fromSession = map.get(socketMsg.getFromUser());
            Session toSession = map.get(socketMsg.getToUser());
            //to sender
            fromSession.getAsyncRemote().sendText(new Gson().toJson(senderMessage));
            //to receiver
            if (toSession != null && !socketMsg.getFromUser().equals(socketMsg.getToUser())) {
                toSession.getAsyncRemote().sendText(new Gson().toJson(receiverMessage));
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 群发自定义消息
     */
    public void broadcast(String message) {
        for (WebSocketService item : webSocketSet) {
            item.session.getAsyncRemote().sendText(message);//异步发送消息.
        }
    }
}
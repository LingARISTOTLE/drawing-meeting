package com.drawing.drawingmeeting.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


@Slf4j
@Component
@ServerEndpoint("/websocket/{username}/{roomId}")
public class WebSocketServer {

    //在线人数
    private static int onlineCount;
    //当前会话
    private Session session;
    //用户唯一标识
    private String username;
    //房间号
    private String roomId;
    //连接集合
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();


    /**
     * 按照房间管理用户
     * 大map的key是房间号，value是该房间内的所有连接
     * 小map表示连接，key是用户的username账号，value是连接Endpoint
     */
    private static ConcurrentHashMap<Integer, ConcurrentHashMap<String, WebSocketServer>> roomMap = new ConcurrentHashMap<>();

    /**
     * 总连接集合，维护所有连接用户
     * concurrent包的线程安全set，用来存放每个客户端对应的MyWebSocket对象
     */
    private static ConcurrentHashMap<String, WebSocketServer> userMap = new ConcurrentHashMap();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username, @PathParam("roomId") String roomId) {
        //将当前连接初始化当前EndPoint对象
        this.session = session;
        //将当前用户初始化当前EndPoint
        this.username = username;
        //将当前用户所在房间好初始化当前EndPoint
        this.roomId = roomId;
        //判断当前房间是否存在
        if (roomMap.containsKey(roomId)) {
            //如果含有当前房间，那么进入房间(将EndPoint添加进集合)
            ConcurrentHashMap<String, WebSocketServer> roomUserMap = roomMap.get(roomId);
            roomUserMap.put(username, this);

            //向总连接集合种添加当前连接
            webSocketSet.add(this);
            //维护用户集合
            if (userMap.containsKey(username)) {
                userMap.remove(username);
                userMap.put(username, this);
            } else {
                userMap.put(username, this);
                addOnlineCount();
            }
        } else {
            //如果不包含当前房间
            //创建房间
            ConcurrentHashMap<String, WebSocketServer> room = new ConcurrentHashMap<>();
            //将自己添加到房间
            room.put(username, this);
            //将房间添加到容器中管理
            int index = Integer.parseInt(roomId);
            roomMap.put(index, room);

            //向总连接集合种添加当前连接
            webSocketSet.add(this);
            //维护用户集合
            if (userMap.containsKey(username)) {
                userMap.remove(username);
                userMap.put(username, this);
            } else {
                userMap.put(username, this);
                addOnlineCount();
            }
        }

        log.info("用户:{} 已连接，当前房间号为{}", this.username, this.roomId);


    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
        log.info("用户:{} 出现异常错误，异常原因{}", this.username, error.getMessage());
    }

    @OnClose
    public void onClose() {
        //移除总连接
        webSocketSet.remove(this);

        //移除会议室
        if (roomMap.containsKey(this.roomId)) {
            ConcurrentHashMap<String, WebSocketServer> roomUserMap = roomMap.get(this.roomId);
            if (roomUserMap.containsKey(this.username)) {
                roomUserMap.remove(this.username);
            }
        }

        //移除用户集合
        if (userMap.containsKey(this.username)) {
            userMap.remove(this.username);
            subOnlineCount();
        }
        log.info("用户:{} 断开连接, 当前连接数:{}", username, getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("用户:{} 收到消息:{}", this.username, message);
    }


    /**
     * 获取当前连接数
     *
     * @return
     */
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    /**
     * 当前连接数加一
     */
    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    /**
     * 当前连接数减一
     */
    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }


    /**
     * 查看当前房间是否存在
     */
    public static boolean isRoomExist(Integer roomId) {
        return roomMap.containsKey(roomId);
    }

}

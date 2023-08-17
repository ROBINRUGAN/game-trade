package com.game_trade.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.game_trade.common.BaseContext;
import com.game_trade.domain.ChatLinks;
import com.game_trade.domain.ChatMessage;
import com.game_trade.service.IChatLinksService;
import com.game_trade.service.IChatMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;


@Component
@ServerEndpoint(value = "/ws/chat/{userId}")
@Slf4j
public class WebSocketServer {

    //@Autowired
    private static IChatMessageService chatMessageService;

    //@Autowired
    private static IChatLinksService chatLinksService;

    private static ApplicationContext applicationContext;

    // 通过ApplicationContext获取bean,因为websocket是单例的，所以可以通过这种方式获取到service
    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }


    // 保存用户的session，key是用户的id
    private static final ConcurrentHashMap<Long, Session> users = new ConcurrentHashMap<>();

    /**
     * 连接成功时调用的方法
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session,@PathParam("userId") Long userId) {
        log.info("有新的连接，userId：{}", userId);
        // 将用户的session保存到map中
        users.put(userId, session);
    }

    /**
     * 关闭连接时调用的方法
     *
     * @param session
     */
    @OnClose
    public void onClose(Session session , @PathParam("userId") Long userId) {
        log.info("连接关闭，userId：{}", userId);
        // 将用户的session从map中移除
        users.remove(userId);
    }

    /**
     * 客户端发送消息时调用的方法
     *
     * @param session
     */
    @OnMessage
    public void onMessage(Session session, String message ) {
        log.info("session：{}", session);
        // 将消息转换为对象
        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessage chatMessage = null;
        try {
            chatMessage = objectMapper.readValue(message, ChatMessage.class);
        } catch (Exception e) {
            log.error("消息转换失败：{}", e.getMessage());
        }
        // 获取接收者的id
        Long toUserId = chatMessage.getRecipientId();
        Long fromUserId = null;
        // 不为系统信息时获取发送者的id
        if (!chatMessage.getType().equals(ChatMessage.TYPE_SYSTEM)) {
            fromUserId = chatMessage.getSenderId();
        }
        // 1 消息：ChatMessage(id=null, senderId=1, recipientId=0, content=你好, type=0, createTime=null, isRead=false)
        log.info("服务器收到来自 ：{} 消息：{}", fromUserId, chatMessage);
        Session toUserSession = users.get(toUserId);
        try{
            if (toUserSession != null) {
                // 发送消息给指定用户
                sendMessage(toUserSession, chatMessage);
            } else {
                log.error("用户：{} 不在线，直接存入历史消息里面", toUserId);
                chatMessageService.save(chatMessage);
            }
        } catch (Exception e) {
            log.error("用户：{} 不在线", toUserId);
        }
    }

    /**
     * 发生错误时调用的方法
     *
     * @param session
     */
    public void onError(Session session, Throwable error , @PathParam("userId") Long userId) {
        users.remove(userId);
        log.error("发生错误：{}", error.getMessage());
    }

    /**
     * 发送消息给指定用户
     *
     * @param session
     * @param chatMessage
     */
    public static void sendMessage(Session session, ChatMessage chatMessage) {
        // 获取service
        chatMessageService = applicationContext.getBean(IChatMessageService.class);
        chatLinksService = applicationContext.getBean(IChatLinksService.class);
        try {
            // 将消息转换为json字符串
            ObjectMapper objectMapper = new ObjectMapper();
            // 设置当前时间
            chatMessage.setCreateTime(LocalDateTime.now());
            // 将LocalDateTime转换为时间戳
            objectMapper.registerModule(new JavaTimeModule());
            // {"id":null,"senderId":1,"recipientId":0,"content":"你好","type":0,"createTime":"2023-05-28 00:36:43","isRead":false}
            String message = objectMapper.writeValueAsString(chatMessage);
            // 发送给：0 发送消息：{"id":null,"senderId":1,"recipientId":0,"content":"你好","type":0,"createTime":"2023-05-28 00:36:43","isRead":false}
            log.info("发送给：{} 发送消息：{}", session.getId(), message);
            // 发送消息
            session.getBasicRemote().sendText(message);
            log.info("发送消息成功");
            // 保存消息到数据库
            chatMessageService.save(chatMessage);
            //log.info("保存消息成功");
            // 添加到消息列表
            ChatLinks chatLinks = new ChatLinks();
            chatLinks.setUserId(chatMessage.getSenderId());
            chatLinks.setChaterId(chatMessage.getRecipientId());
            log.info(chatLinks.toString());
            chatLinksService.addChatLink(chatLinks);

        } catch (Exception e) {
            log.error("发送消息失败：{}", e);
        }
    }

    /**
     * 发送系统消息给指定用户,外部调用
     *
     * @param senderId
     * @param recipientId
     * @param message
     */
    public static void sendSystemMessage(Long senderId, Long recipientId, String message) {
        // 封装消息
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId(senderId);
        chatMessage.setRecipientId(recipientId);
        chatMessage.setType(ChatMessage.TYPE_SYSTEM);
        chatMessage.setContent(message);
        chatMessage.setIsRead(ChatMessage.READ_FALSE);
        // 获取接收者的session
        Session toUserSession = users.get(recipientId);
        if (toUserSession != null) {
            // 发送消息给指定用户
            sendMessage(toUserSession, chatMessage);
        } else {
            log.error("用户：{} 不在线，直接存入历史记录", recipientId);
            chatMessageService.save(chatMessage);
        }
    }
}


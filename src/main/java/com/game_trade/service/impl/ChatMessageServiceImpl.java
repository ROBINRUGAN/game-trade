package com.game_trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.game_trade.domain.ChatMessage;
import com.game_trade.dao.ChatMessageDao;
import com.game_trade.service.IChatMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 翁鹏
 * @since 2023-05-09
 */
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageDao, ChatMessage> implements IChatMessageService {


    /**
     * 获得聊天记录
     * @param userId
     * @param chaterId
     * @return
     */
    @Override
    public List<ChatMessage> getChatHistory(Long userId, Long chaterId) {
        // 条件构造器
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        // 查询条件：当前用户id为发送者id并且对方用户id为接收者id 或者 当前用户id为接收者id并且对方用户id为发送者id
        wrapper.eq(ChatMessage::getSenderId, userId).eq(ChatMessage::getRecipientId, chaterId).or().eq(ChatMessage::getSenderId, chaterId).eq(ChatMessage::getRecipientId, userId);
        // 得到聊天记录
        List<ChatMessage> chatHistory = this.list(wrapper);
        // 更改为已读
        chatHistory.forEach(chatMessage -> {
            if (chatMessage.getRecipientId().equals(userId)) {
                chatMessage.setIsRead(ChatMessage.READ_TRUE);
                this.updateById(chatMessage);
            }
        });
        // 按时间降序排序
        chatHistory.sort(Comparator.comparing(ChatMessage::getCreateTime).reversed());
        // 返回聊天记录
        return chatHistory;
    }
}

package com.game_trade.service;

import com.game_trade.domain.ChatMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 翁鹏
 * @since 2023-05-09
 */
public interface IChatMessageService extends IService<ChatMessage> {
    /**
     * 获取聊天记录
     * @param userId
     * @param chaterId
     * @return
     */
    public List<ChatMessage> getChatHistory(Long userId, Long chaterId);

}

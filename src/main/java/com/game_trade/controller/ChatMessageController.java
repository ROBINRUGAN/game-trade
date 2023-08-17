package com.game_trade.controller;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.game_trade.common.Code;
import com.game_trade.domain.ChatMessage;
import com.game_trade.service.IChatMessageService;
import com.game_trade.utils.OSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.game_trade.domain.ChatMessage.READ_TRUE;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 翁鹏
 * @since 2023-05-09
 */
@RestController
@RequestMapping("/chatMessage")
@Slf4j
public class ChatMessageController {

    @Autowired
    private IChatMessageService chatMessageService;

    /**
     * 上传聊天图片
     *
     * @param file
     */
    @PostMapping("/uploadChatImage")
    public SaResult uploadChatImage(MultipartFile file) throws IOException {
        String url = OSSUtil.uploadFile(file);
        return SaResult.data(url);
    }

    /**
     * 获得聊天记录
     * @param chaterId
     * @return
     */
    @GetMapping("/getChatHistory")
    public SaResult getChatHistory(Long chaterId) {
        Long userId = StpUtil.getLoginIdAsLong();
        List<ChatMessage> chatHistory = chatMessageService.getChatHistory(userId, chaterId);
        chatHistory.forEach(chatMessage -> {
            if (chatMessage.getRecipientId().equals(userId)) {
                chatMessage.setIsRead(READ_TRUE);
                chatMessageService.updateById(chatMessage);
            }
        });
        return SaResult.data(chatHistory);
    }

    /**
     * 将某条消息设置为已读
     * @param messageId
     * @return
     */
    @PostMapping ("/getMessageRead")
    public SaResult getMessageRead(Long messageId) {
        ChatMessage message = chatMessageService.getById(messageId);
        if(message!=null)
        {
            message.setIsRead(READ_TRUE);
            chatMessageService.updateById(message);
            return SaResult.ok("已设置为已读");
        }
        else
        {
            log.error("没有找到对应id的这条消息！");
            return SaResult.error("没有找到对应id的这条消息！");
        }
    }
}


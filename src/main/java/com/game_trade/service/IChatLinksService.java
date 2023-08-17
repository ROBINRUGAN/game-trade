package com.game_trade.service;

import com.game_trade.common.BaseUserInfoResult;
import com.game_trade.domain.ChatLinks;
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
public interface IChatLinksService extends IService<ChatLinks> {
    /**
     * 新增聊天关系，即对话列表，不重复
     * @param chatLinks
     */
    public void addChatLink(ChatLinks chatLinks);

    /**
     * 删除聊天关系，即对话列表
     * @param chatLinks
     */
    public void deleteChatLink(ChatLinks chatLinks);

    /**
     * 查询当前用户的所有聊天关系，即对话框
     * @param userId
     * @return
     */
    public List<BaseUserInfoResult> selectChatLinks(Long userId);

}

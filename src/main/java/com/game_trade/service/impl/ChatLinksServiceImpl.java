package com.game_trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.game_trade.common.BaseContext;
import com.game_trade.common.BaseUserInfoResult;
import com.game_trade.domain.ChatLinks;
import com.game_trade.dao.ChatLinksDao;
import com.game_trade.service.IChatLinksService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.game_trade.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 翁鹏
 * @since 2023-05-09
 */
@Service
public class ChatLinksServiceImpl extends ServiceImpl<ChatLinksDao, ChatLinks> implements IChatLinksService {

    @Autowired
    private IUserService userService;

    /**
     * 新增聊天关系，即对话列表，不重复
     * @param chatLinks
     */
    @Override
    public void addChatLink(ChatLinks chatLinks) {
        // 条件构造器
        LambdaQueryWrapper<ChatLinks> wrapper = new LambdaQueryWrapper<>();
        // 查询条件：当前用户id和对方用户id
        wrapper.eq(ChatLinks::getUserId, chatLinks.getUserId()).eq(ChatLinks::getChaterId, chatLinks.getChaterId());
        // 查询不存在则新增
        if (this.getOne(wrapper) == null) {
            this.save(chatLinks);
        }
    }

    /**
     * 删除聊天关系，即对话列表
     * @param chatLinks
     */
    @Override
    public void deleteChatLink(ChatLinks chatLinks) {
        // 条件构造器
        LambdaQueryWrapper<ChatLinks> wrapper = new LambdaQueryWrapper<>();
        // 查询条件：当前用户id和对方用户id
        wrapper.eq(ChatLinks::getUserId, chatLinks.getUserId()).eq(ChatLinks::getChaterId, chatLinks.getChaterId());
        // 查询存在则删除
        if (this.getOne(wrapper) != null) {
            this.remove(wrapper);
        }

    }

    /**
     * 查询当前用户的所有聊天关系，即对话框
     *
     * @param userId
     * @return
     */
    @Override
    public List<BaseUserInfoResult> selectChatLinks(Long userId) {
        // 有两种情况，一是当前用户是发起者，二是当前用户是接收者
        // 条件构造器
        LambdaQueryWrapper<ChatLinks> wrapper = new LambdaQueryWrapper<>();
        // 查询条件：当前用户id作为发起者
        wrapper.eq(ChatLinks::getUserId, userId);
        // 查询结果1,存放接收者id
        List<Long> list1 = this.list(wrapper).stream().map(ChatLinks::getChaterId).collect(Collectors.toList());
        // 查询条件：当前用户id作为接收者
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatLinks::getChaterId, userId);
        // 查询结果2.存放发起者id
        List<Long> list2 = this.list(wrapper).stream().map(ChatLinks::getUserId).collect(Collectors.toList());
        // 将结果1的接受者id和结果2的发起者id放入list，不重复
        for (Long chaterId : list1) {
            if (!list2.contains(chaterId)) {
                list2.add(chaterId);
            }
        }
        // 最终结果
        List<BaseUserInfoResult> list = list2.stream().map(id -> userService.getUserInfoById(id)).collect(Collectors.toList());
        return list;
    }
}

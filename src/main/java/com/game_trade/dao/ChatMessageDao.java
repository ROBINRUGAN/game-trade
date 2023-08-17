package com.game_trade.dao;

import com.game_trade.domain.ChatMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 翁鹏
 * @since 2023-05-09
 */
@Mapper
public interface ChatMessageDao extends BaseMapper<ChatMessage> {

}

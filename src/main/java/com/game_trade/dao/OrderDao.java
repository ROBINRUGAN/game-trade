package com.game_trade.dao;

import com.game_trade.domain.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 翁鹏
 * @since 2023-04-10
 */
@Mapper
public interface OrderDao extends BaseMapper<Orders> {

}

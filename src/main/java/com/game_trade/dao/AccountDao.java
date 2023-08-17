package com.game_trade.dao;

import com.game_trade.domain.Account;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 吴荣榜
 * @since 2023-04-22
 */
@Mapper
public interface AccountDao extends BaseMapper<Account> {

}

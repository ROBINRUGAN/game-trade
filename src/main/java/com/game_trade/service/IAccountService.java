package com.game_trade.service;

import com.game_trade.domain.Account;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 吴荣榜
 * @since 2023-04-22
 */
public interface IAccountService extends IService<Account> {

    /**
     * 通过用户id更新账户信息
     * @param account
     */
    void updateByUserId(Account account);
}

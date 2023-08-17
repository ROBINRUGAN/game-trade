package com.game_trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.game_trade.domain.Account;
import com.game_trade.dao.AccountDao;
import com.game_trade.domain.User;
import com.game_trade.service.IAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.game_trade.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 吴荣榜
 * @since 2023-04-22
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountDao, Account> implements IAccountService
{
    @Autowired
    IUserService userService;

    /**
     * 通过用户id更新账户信息
     * @param account
     */
    @Override
    public void updateByUserId(Account account)
    {
        //条件构造器,用来查询是否存在这个钱包
        LambdaQueryWrapper<Account> queryWrapperAccount = new LambdaQueryWrapper<>();
        queryWrapperAccount.eq(Account::getUserId, account.getUserId());
        Account accountTemp = new Account();
        if (this.getOne(queryWrapperAccount) == null)
        {
            User user = userService.getById(account.getUserId());

            accountTemp.setUserId(user.getId());
            accountTemp.setStatus(user.getRole().equals("black") ? 0 : 1);
            accountTemp.setBalance(BigDecimal.valueOf(0));
            this.save(accountTemp);
        }
        else
        {
            accountTemp = this.getOne(queryWrapperAccount);
        }
        account.setBalance(account.getBalance().add(accountTemp.getBalance()));
        this.update(account, queryWrapperAccount);
    }
}

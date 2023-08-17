package com.game_trade.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.game_trade.domain.Account;
import com.game_trade.domain.User;
import com.game_trade.service.IAccountService;
import com.game_trade.service.IUserService;
import com.game_trade.service.impl.AccountServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 吴荣榜
 * @since 2023-04-22
 */
@RestController
@RequestMapping("/account")
@Slf4j
public class AccountController
{
    @Autowired
    private IAccountService accountService;

    @Autowired
    private IUserService userService;

    @PostMapping("/recharge")
    @ApiOperation("钱包充值")
    public SaResult recharge(@RequestBody Account account)
    {
        Long userId = StpUtil.getLoginIdAsLong();
        account.setUserId(userId);
        accountService.updateByUserId(account);
        return SaResult.ok("充值成功！");
    }

    @PostMapping("/withdraw")
    @ApiOperation("钱包提现")
    public SaResult withdraw(BigDecimal money)
    {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getUserId, userId);
        Account account = accountService.getOne(queryWrapper);
        account.setBalance(account.getBalance().subtract(money));
        //将对应用户的钱包更新
        accountService.update(account, queryWrapper);
        return SaResult.ok("提现成功！");
    }

    /**
     * 获取当前用户的余额
     * @return
     */
    @GetMapping
    @ApiOperation("获取当前用户的余额")
    public SaResult getBalance() {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getUserId, userId);
        Account account = accountService.getOne(queryWrapper);
        return SaResult.data(account.getBalance());
    }
}


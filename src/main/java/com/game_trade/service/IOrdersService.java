package com.game_trade.service;

import com.game_trade.common.GameAccountInfo;
import com.game_trade.domain.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 翁鹏
 * @since 2023-04-10
 */
public interface IOrdersService extends IService<Orders> {

    /**
     * 提交订单
     * @param orders
     */
    void submit(Orders orders);
    /**
     * 取消订单
     * @param id
     */
    boolean cancel(Long id);

    /**
     * 支付订单
     * @param userId
     */
    List<Orders> unpaidList(Long userId);

    /**
     * 支付订单
     * @param userId
     */
    List<Orders> paidList(Long userId);

    /**
     * 支付订单
     * @param userId
     */
    List<Orders> completedList(Long userId);

    /**
     * 支付订单
     * @param userId
     */
    List<Orders> canceledList(Long userId);

    /**
     * 发送游戏账号信息邮箱
     * @param gameAccountInfo
     * @param toEmail
     */
    void sendGameAccountEmail(GameAccountInfo gameAccountInfo, String toEmail);

    /**
     * 商家获取已经付款的订单
     * @param userId
     * @return
     */
    List<Orders> paidOrderList(Long userId);
}

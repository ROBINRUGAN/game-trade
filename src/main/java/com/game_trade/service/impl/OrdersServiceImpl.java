package com.game_trade.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.game_trade.common.GameAccountInfo;
import com.game_trade.domain.Orders;
import com.game_trade.dao.OrderDao;
import com.game_trade.service.ICommodityService;
import com.game_trade.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 翁鹏
 * @since 2023-04-10
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrderDao, Orders> implements IOrdersService {

    //	引入邮件接口
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ICommodityService commodityService;


    /**
     * 提交订单
     *
     * @param orders
     */
    @Override
    public void submit(Orders orders) {
        // 获得当前登录用户
        Long userId = StpUtil.getLoginIdAsLong();
        // 设置订单用户
        orders.setBuyerId(userId);
        // 设置下单时间
        orders.setOrderTime(LocalDateTime.now());
        // 设置订单状态
        orders.setStatus(Orders.STATUS_WAIT_PAY);
        // 设置卖家id
        orders.setSellerId(commodityService.getById(orders.getCommodityId()).getSellerId());
        // 保存订单
        this.save(orders);

    }

    /**
     * 取消订单
     *
     * @param id
     */
    @Override
    public boolean cancel(Long id) {
        // 获得当前登录用户
        Long userId = StpUtil.getLoginIdAsLong();
        // 查询订单
        Orders orders = this.getOne(new LambdaQueryWrapper<Orders>().eq(Orders::getId, id).eq(Orders::getBuyerId, userId));
        // 判断订单状态
        if (Objects.equals(orders.getStatus(), Orders.STATUS_WAIT_PAY)) {
            // 设置订单状态
            orders.setStatus(Orders.STATUS_CANCEL);
            // 更新订单
            this.updateById(orders);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 查询用户未支付订单
     *
     * @param userId
     * @return
     */
    @Override
    public List<Orders> unpaidList(Long userId) {
        return this.list(new LambdaQueryWrapper<Orders>().eq(Orders::getBuyerId, userId).eq(Orders::getStatus, Orders.STATUS_WAIT_PAY));
    }

    /**
     * 查询用户已支付订单
     *
     * @param userId
     * @return
     */
    @Override
    public List<Orders> paidList(Long userId) {
        return this.list(new LambdaQueryWrapper<Orders>().eq(Orders::getBuyerId, userId).eq(Orders::getStatus, Orders.STATUS_PAID));
    }

    /**
     * 查询用户已完成订单
     *
     * @param userId
     * @return
     */
    @Override
    public List<Orders> completedList(Long userId) {
        return this.list(new LambdaQueryWrapper<Orders>().eq(Orders::getBuyerId, userId).eq(Orders::getStatus, Orders.STATUS_COMPLETE));
    }

    /**
     * 查询用户已取消订单
     *
     * @param userId
     * @return
     */
    @Override
    public List<Orders> canceledList(Long userId) {
        return this.list(new LambdaQueryWrapper<Orders>().eq(Orders::getBuyerId, userId).eq(Orders::getStatus, Orders.STATUS_CANCEL));
    }

    /**
     * 商家获取已经付款的订单
     *
     * @param userId
     * @return
     */
    @Override
    public List<Orders> paidOrderList(Long userId)
    {
        return this.list(new LambdaQueryWrapper<Orders>().eq(Orders::getSellerId,userId).eq(Orders::getStatus,Orders.STATUS_PAID));
    }

    /**
     * 发送邮件
     *
     * @param gameAccountInfo
     * @param toEmail
     */
    @Value("${spring.mail.username}")
    private String formEmail;
    @Override
    public void sendGameAccountEmail(GameAccountInfo gameAccountInfo, String toEmail) {
        // 创建邮件信息
        SimpleMailMessage emailMessage = new SimpleMailMessage();
        // 设置邮件发送人
        emailMessage.setFrom(formEmail);
        // 设置邮件接收人
        emailMessage.setTo(toEmail);
        // 设置邮件主题
        emailMessage.setSubject("好二游");
        // 设置邮件内容 账号+密码+备注
        String text = "账号：" + gameAccountInfo.getGameAccount() + "\n密码：" + gameAccountInfo.getGamePassword() + "\n备注：" + gameAccountInfo.getRemark();
        emailMessage.setText(text);
        // 发送邮件
        mailSender.send(emailMessage);
    }
}

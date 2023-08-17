package com.game_trade.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.game_trade.common.Code;
import com.game_trade.component.WebSocketServer;
import com.game_trade.domain.Account;
import com.game_trade.domain.Commodity;
import com.game_trade.domain.Orders;
import com.game_trade.domain.User;
import com.game_trade.dto.GameAccountInfoDto;
import com.game_trade.service.IAccountService;
import com.game_trade.service.ICommodityService;
import com.game_trade.service.IOrdersService;
import com.game_trade.service.IUserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 翁鹏
 * @since 2023-04-10
 */
@RestController
@RequestMapping("/orders")
@Slf4j
@SaCheckRole(value = {User.ROLE_ADMIN, User.ROLE_WHITE} , mode = SaMode.OR)
public class OrderController {
    @Autowired
    private IOrdersService orderService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ICommodityService commodityService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 用户下单
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public SaResult submit(@RequestBody Orders orders) {
        log.info("订单数据：{}", orders);
        // 确认商品是否被删除，或者在审核中
        Commodity commodity = commodityService.getById(orders.getCommodityId());
        if (commodity == null || commodity.getIsDeleted().equals(Commodity.IS_DELETED_YES) || commodity.getStatus().equals(Commodity.STATUS_SOLD_OUT)) {
            return SaResult.error("商品不存在");
        }
        if (!commodity.getStatus().equals(Commodity.IS_DELETED_YES)) {
            return SaResult.error("商品审核未通过中");
        }
        // 判断商品价格是否发生变化
        if (commodity.getPrice().compareTo(orders.getAmount()) != 0) {
            return SaResult.error("商品价格发生变化");
        }
        orderService.submit(orders);
        return SaResult.get(Code.OK, "下单成功", orders);
    }


    /**
     * 用户支付订单
     *
     * @param orderId
     * @return
     */
    @PostMapping("/payBill")
    @ApiOperation("用户支付订单")
    public SaResult payBill(Long orderId) {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Account> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Account::getUserId, userId);
        Orders orders = orderService.getById(orderId);
        Account user = accountService.getOne(lambdaQueryWrapper);

        Map<String, String> map = new HashMap<>();
        map.put("preBalance", user.getBalance().toString());
        map.put("orderMoney", orders.getAmount().toString());
        if (user.getBalance().compareTo(orders.getAmount()) >= 0) {
            user.setBalance(user.getBalance().subtract(orders.getAmount()));
            map.put("laterBalance", user.getBalance().toString());
            //更新用户余额到数据库
            accountService.update(user, lambdaQueryWrapper);
            //更新订单状态
            orders.setStatus(Orders.STATUS_PAID);
            orderService.updateById(orders);
            //更新商品状态
            Commodity commodity = commodityService.getById(orders.getCommodityId());
            commodity.setStatus(Commodity.STATUS_SOLD_OUT);
            String key1 = "commodity_" + commodity.getSellerId() +"_" + Commodity.STATUS_AUDIT_SUCCESS;
            // 清除缓存
            String key2 = "commodity_" + commodity.getId();
            // 清空缓存
            redisTemplate.delete(key1);
            redisTemplate.delete(key2);
            commodityService.updateById(commodity);
            return SaResult.get(Code.OK, "支付成功!", map);
        }
        else {
            return SaResult.get(Code.OK, "余额不足,无法支付!", map);
        }
    }

    /**
     * 用户取消订单
     *
     * @param id
     * @return
     */
    @PostMapping("/cancel")
    @ApiOperation("用户取消订单")
    public SaResult cancel(@RequestParam Long id) {
        log.info("订单数据：{}", id);
        if (orderService.cancel(id)) {
            return SaResult.ok("取消成功");
        }
        else {
            return SaResult.ok("无法取消,订单未处于未审核状态");
        }
    }

    /**
     * 根据订单id查询订单详情
     *
     * @param id
     * @return
     */
    @GetMapping
    @ApiOperation("根据订单id查询订单详情")
    public SaResult get(@RequestParam Long id) {
        Orders order = orderService.getById(id);
        return SaResult.get(Code.OK, "查询成功", order);
    }

    /**
     * 根据用户id查询未支付的订单列表
     *
     * @param
     * @return
     */
    @GetMapping("/unpaidList")
    @ApiOperation("根据用户id查询未支付的订单列表")
    public SaResult unpaidList() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<Orders> ordersList = orderService.unpaidList(userId);
        return SaResult.get(Code.OK, "查询成功", ordersList);
    }

    /**
     * 根据用户id查询已支付的订单列表
     *
     * @param
     * @return
     */
    @GetMapping("/paidList")
    @ApiOperation("根据用户id查询已支付的订单列表")
    public SaResult paidList() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<Orders> ordersList = orderService.paidList(userId);
        return SaResult.get(Code.OK, "查询成功", ordersList);
    }

    /**
     * 根据用户id查询已完成的订单列表
     *
     * @param
     * @return
     */
    @GetMapping("/completedList")
    @ApiOperation("根据用户id查询已完成的订单列表")
    public SaResult completedList() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<Orders> ordersList = orderService.completedList(userId);
        return SaResult.get(Code.OK, "查询成功", ordersList);
    }

    /**
     * 根据用户id查询已取消的订单列表
     *
     * @param
     * @return
     */
    @GetMapping("/canceledList")
    @ApiOperation("根据用户id查询已取消的订单列表")
    public SaResult canceledList() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<Orders> ordersList = orderService.canceledList(userId);
        return SaResult.get(Code.OK, "查询成功", ordersList);
    }

    /**
     * 商家获取已经付款的订单
     *
     * @return
     */
    @GetMapping("/paidOrder")
    @ApiOperation("商家获取已经付款的订单")
    public SaResult paidOrder() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<Orders> list = orderService.paidOrderList(userId);
        return SaResult.get(Code.OK, "商家查询已付款的订单", list);
    }

    /**
     * 商家发货,完成订单
     *
     * @param gameAccountInfoDto
     * @return
     */
    @PostMapping("/finishOrder")
    @ApiOperation("商家发货,完成订单")
    public SaResult finishOrder(GameAccountInfoDto gameAccountInfoDto) {
        Orders order = orderService.getById(gameAccountInfoDto.getOrderId());
        Commodity commodity = commodityService.getById(order.getCommodityId());
        //判断是否是已经付款的订单
        if (!order.getStatus().equals(Orders.STATUS_PAID)) {
            return SaResult.error("订单未付款");
        }
        //要对应的卖家才有权限
        if (commodity.getSellerId().equals(StpUtil.getLoginIdAsLong())) {
            //得到买家的邮箱
            User buyer = userService.getById(order.getBuyerId());
            String toEmail = buyer.getEmail();
            //发送邮件
            orderService.sendGameAccountEmail(gameAccountInfoDto, toEmail);
            //更新订单状态
            order.setStatus(Orders.STATUS_COMPLETE);
            order.setCheckoutTime(LocalDateTime.now());
            orderService.updateById(order);
            return SaResult.ok("发货完成");
        }
        else {
            return SaResult.ok("不是对应商家,无权限处理");
        }
    }

    /**
     * 用户确认收货
     *
     * @param orderId
     * @return
     */
    @PostMapping("/confirmOrder")
    @ApiOperation("用户确认收货")
    public SaResult confirmOrder(Long orderId) {
        Orders orders = orderService.getById(orderId);
        //必须限定是卖家已发货
        if (orders.getStatus().equals(Orders.STATUS_COMPLETE)) {
            orders.setStatus(Orders.STATUS_FINISH);
            orderService.updateById(orders);

            LambdaQueryWrapper<Account> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Account::getUserId, orders.getSellerId());
            Account seller = accountService.getOne(lambdaQueryWrapper);
            seller.setBalance(seller.getBalance().add(orders.getAmount()));
            accountService.update(seller, lambdaQueryWrapper);
            return SaResult.ok("成功确认收货");
        }
        else {
            return SaResult.ok("收货失败，当前订单状态不是待确认收货");
        }
    }

    /**
     * 用户获取确认收货的订单
     * @return
     *
     */
    @GetMapping("/confirmOrder")
    @ApiOperation("用户获取确认收货的订单")
    public SaResult getConfirmOrder() {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getBuyerId, userId);
        lambdaQueryWrapper.eq(Orders::getStatus, Orders.STATUS_FINISH);
        List<Orders> list = orderService.list(lambdaQueryWrapper);
        return SaResult.get(Code.OK, "用户获取确认收货的订单", list);
    }

    /**
     * 用户申请退货
     *
     * @param orderId
     * @return
     */
    @PostMapping("/back")
    @ApiOperation("买家申请退货")
    public SaResult getBack(Long orderId) {
        Orders orders = orderService.getById(orderId);
        //只有完成了的订单才可以退货
        if (orders.getStatus().equals(Orders.STATUS_FINISH)) {
            orders.setStatus(Orders.STATUS_WAIT_BACK);
            orderService.updateById(orders);
            return SaResult.ok("成功申请退货，待审核");
        }
        else {
            return SaResult.ok("无法申请退货");
        }
    }

    /**
     * 卖家获取退货订单
     * @return
     *
     */
    @GetMapping("/back")
    @ApiOperation("卖家获取退货订单")
    public SaResult getBackOrder() {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getSellerId, userId);
        lambdaQueryWrapper.eq(Orders::getStatus, Orders.STATUS_WAIT_BACK);
        List<Orders> list = orderService.list(lambdaQueryWrapper);
        return SaResult.get(Code.OK, "卖家获取退货订单", list);
    }




    /**
     * 卖家处理退货类型
     *
     * @param orderId
     * @param extent
     * @return
     */
    @PostMapping("/processBack")
    @ApiOperation("卖家处理退货类型")
    public SaResult processBack(Long orderId, Integer extent) {
        Orders orders = orderService.getById(orderId);
        if (orders.getStatus().equals(Orders.STATUS_WAIT_BACK)) {
            //这里卖家自行检查账户情况
            if (extent.equals(Orders.SLIGHT_DAMAGE)) {
                orders.setStatus(Orders.SLIGHT_DAMAGE);
                orderService.updateById(orders);
                return SaResult.ok("轻度受损，仅退回一半金额");
            }
            else if (extent.equals(Orders.SERIOUS_DAMAGE)) {
                orders.setStatus(Orders.SERIOUS_DAMAGE);
                orderService.updateById(orders);
                return SaResult.ok("重度受损，不退回金额");
            }
            else if (extent.equals(Orders.NO_DAMAGE)) {
                orders.setStatus(Orders.NO_DAMAGE);
                orderService.updateById(orders);
                return SaResult.ok("无受损，全额退款");
            }
            else if(extent.equals(Orders.REFUSE_BACK))
            {
                orders.setStatus(Orders.REFUSE_BACK);
                orderService.updateById(orders);
                return SaResult.ok("商家拒绝退货！");
            }
            else {
                return SaResult.ok("传参错误");
            }
        }
        else {
            return SaResult.ok("订单状态异常！");
        }
    }
}

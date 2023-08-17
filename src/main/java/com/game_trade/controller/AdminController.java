package com.game_trade.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.game_trade.common.Code;
import com.game_trade.common.PageResult;
import com.game_trade.component.WebSocketServer;
import com.game_trade.domain.*;
import com.game_trade.service.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@Slf4j
@SaCheckRole(User.ROLE_ADMIN)
public class AdminController {
    @Autowired
    private ICommodityService commodityService;

    @Autowired
    private IComplaintsService complaintsService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IOrdersService ordersService;

    // 获得管理员id
    private Long getAdminId() {
        return StpUtil.getLoginIdAsLong();
    }

    /**
     * 分页查询审核中的商品
     *
     * @return
     */
    @GetMapping("/pageCheck")

    @ApiOperation("分页查询审核中的商品")
    public SaResult pageCheck(@RequestParam(required = false, defaultValue = "1") Integer currentPage, @RequestParam(required = false, defaultValue = "10") Integer pageSize, String commodityKey) {
        PageResult pageResult = commodityService.pageCommodity(currentPage, pageSize, Commodity.STATUS_WAIT_AUDIT, commodityKey);
        return SaResult.get(Code.OK, "查询成功", pageResult);

    }


    /**
     * 分页查询审核通过的商品
     *
     * @return
     */
    @GetMapping("/pagePass")
    @ApiOperation("分页查询审核通过的商品")
    public SaResult pagePass(@RequestParam(required = false, defaultValue = "1") int currentPage, @RequestParam(required = false, defaultValue = "10") int pageSize, String commodityKey) {
        PageResult pageResult = commodityService.pageCommodity(currentPage, pageSize, Commodity.STATUS_AUDIT_SUCCESS, commodityKey);
        return SaResult.get(Code.OK, "查询成功", pageResult);
    }

    /**
     * 分页查询审核未通过的商品
     *
     * @return
     */
    @GetMapping("/pageNoPass")
    @ApiOperation("分页查询审核未通过的商品")
    public SaResult pageNoPass(@RequestParam(required = false, defaultValue = "1") int currentPage, @RequestParam(required = false, defaultValue = "10") int pageSize, String commodityKey) {
        PageResult pageResult = commodityService.pageCommodity(currentPage, pageSize, Commodity.STATUS_AUDIT_FAIL, commodityKey);
        return SaResult.get(Code.OK, "查询成功", pageResult);
    }

    /**
     * 审核通过商品
     *
     * @param commodityId
     */
    @PutMapping("/pass")
    @ApiOperation("审核通过商品")
    public SaResult pass(Long commodityId) {
        adminService.pass(commodityId);
        // 获得商品信息
        Commodity commodity = commodityService.getById(commodityId);
        // 通知用户商品审核通过
        try{
            WebSocketServer.sendSystemMessage(getAdminId(), commodity.getSellerId(), "您的商品 : [" + commodity.getCommodityName() + "] 已通过审核");
        }catch (Exception e){
            log.error("websocket发送消息失败");
            return SaResult.ok("待对方上线可接受到通知");
        }
        return SaResult.ok("审核通过");
    }

    /**
     * 审核未通过商品
     *
     * @param commodityId
     */
    @PutMapping("/noPass")
    @ApiOperation("驳回商品")
    public SaResult noPass(Long commodityId) {
        adminService.noPass(commodityId);
        // 获得商品信息
        Commodity commodity = commodityService.getById(commodityId);
        // 通知用户商品审核未通过
        try{
            WebSocketServer.sendSystemMessage(getAdminId(), commodity.getSellerId(), "您的商品 : [" + commodity.getCommodityName() + "] 未通过审核");
        }catch (Exception e){
            log.error("websocket发送消息失败");
            return SaResult.ok("待对方上线可接受到通知");
        }
        return SaResult.ok("审核未通过");

    }

    /**
     * 管理员获得举报列表
     *
     * @return
     */
    @GetMapping("/complainedList")
    @ApiOperation("管理员获得举报列表")
    public SaResult complainedList(@RequestParam(required = false, defaultValue = "1") int currentPage, @RequestParam(required = false, defaultValue = "10") int pageSize, int type) {
        PageResult<Complaints> pageResult = complaintsService.pageComplaint(currentPage, pageSize, type);
        return SaResult.get(Code.OK, "查询成功", pageResult);
    }


    /**
     * 第一个事故处理，商家找回账号"
     *
     * @param complainedId
     * @return
     */

    @PostMapping("/sellerWithdraw")
    @ApiOperation("第一个事故处理，商家找回账号")
    public SaResult sellerWithdraw(Long complainedId) {
        Complaints complaints = complaintsService.getById(complainedId);

        //获取卖家对象
        LambdaQueryWrapper<Account> sellerAccountQueryWrapper = new LambdaQueryWrapper<>();
        sellerAccountQueryWrapper.eq(Account::getUserId, complaints.getComplainedId());
        Account seller = accountService.getOne(sellerAccountQueryWrapper);

        Commodity goods = commodityService.getById(complaints.getCommodityId());

        Map<String, String> map = new HashMap<>();
        map.put("sellerPreMoney", seller.getBalance().toString());
        map.put("goodPrice", goods.getPrice().toString());

        if (seller.getBalance().compareTo(goods.getPrice()) > 0) {
            seller.setBalance(seller.getBalance().subtract(goods.getPrice()));
            map.put("sellerLaterMoney", seller.getBalance().toString());

            accountService.update(seller, sellerAccountQueryWrapper);
            return SaResult.get(Code.OK, "商家价格足够支付", map);
        }
        else {
            seller.setStatus(0);
            accountService.update(seller, sellerAccountQueryWrapper);
            User sellerUser = userService.getById(seller.getUserId());
            sellerUser.setRole(User.ROLE_BLACK);
            userService.updateById(sellerUser);
            return SaResult.get(Code.OK, "商家价格不够支付,将拉黑商家", map);
        }

    }

    @GetMapping ("/getBackList")
    @ApiOperation("获得退货的订单审核列表")
    public SaResult getBackList(@RequestParam(required = false,defaultValue = "1")int currentPage,@RequestParam(required = false,defaultValue = "10")int pageSize)
    {
        PageResult<Orders> list = adminService.getBackList(currentPage,pageSize);
        return SaResult.get(Code.OK,"查询成功",list);
    }

    /**
     * 第二个事故处理第三步，管理员审核
     * @param orderId
     * @return
     */
    @PostMapping("checkGetBack")
    @ApiOperation("管理员审核退货")
    public SaResult checkGetBack(Long orderId) {
        Orders orders = ordersService.getById(orderId);
        //获取卖家买家实体
        LambdaQueryWrapper<Account> sellerQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Account> buyerQueryWrapper = new LambdaQueryWrapper<>();
        sellerQueryWrapper.eq(Account::getUserId, StpUtil.getLoginIdAsLong());
        buyerQueryWrapper.eq(Account::getUserId, orders.getBuyerId());
        Account seller = accountService.getOne(sellerQueryWrapper);
        Account buyer = accountService.getOne(buyerQueryWrapper);
        if (orders.getStatus().equals(Orders.REFUSE_BACK)) {
            orders.setStatus(Orders.STATUS_FINISH_BACK);
            ordersService.updateById(orders);
            try{
                WebSocketServer.sendSystemMessage(getAdminId(),buyer.getUserId(),"商家拒绝退货！");
            }catch (Exception e){
                log.error("websocket发送消息失败");
                return SaResult.ok("待对方上线可接受到通知");
            }
            return SaResult.ok("商家拒绝退货！");
        }
        else if (orders.getStatus().equals(Orders.SLIGHT_DAMAGE)) {
            //以一半的价格补偿卖家
            seller.setBalance(seller.getBalance().subtract(orders.getAmount().divide(BigDecimal.valueOf(2))));
            buyer.setBalance(buyer.getBalance().add(orders.getAmount().divide(BigDecimal.valueOf(2))));
            accountService.update(seller, sellerQueryWrapper);
            accountService.update(buyer, buyerQueryWrapper);
            orders.setStatus(Orders.STATUS_FINISH_BACK);
            ordersService.updateById(orders);
            try{
                WebSocketServer.sendSystemMessage(getAdminId(),buyer.getUserId(),"轻度受损，仅退回一半金额");
            }catch (Exception e){
                log.error("websocket发送消息失败");
                return SaResult.ok("待对方上线可接受到通知");
            }
            return SaResult.ok("轻度受损，仅退回一半金额");
        }
        else if (orders.getStatus().equals(Orders.SERIOUS_DAMAGE)) {
            //将用户拉入黑名单
            User buyerUser = userService.getById(orders.getBuyerId());
            buyerUser.setRole("black");
            userService.updateById(buyerUser);
            try {
                WebSocketServer.sendSystemMessage(getAdminId(),buyer.getUserId(),"恶意毁坏账号，你已被拉黑！！");
            } catch (Exception e) {
                return SaResult.ok("待对方上线可接受到通知");
            }

            orders.setStatus(Orders.STATUS_FINISH_BACK);
            ordersService.updateById(orders);


            return SaResult.ok("重度受损，不退回金额");
        }
        else if (orders.getStatus().equals(Orders.NO_DAMAGE)) {

            orders.setStatus(Orders.STATUS_FINISH_BACK);
            ordersService.updateById(orders);
            try {
                WebSocketServer.sendSystemMessage(getAdminId(),buyer.getUserId(),"无受损，全额退款");
            } catch (Exception e) {
                return SaResult.ok("待对方上线可接受到通知");
            }

            return SaResult.ok("无受损，全额退款");
        }
        else
        {
            return SaResult.ok("订单状态异常");
        }
    }
}

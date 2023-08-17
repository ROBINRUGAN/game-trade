package com.game_trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.game_trade.common.PageResult;
import com.game_trade.controller.OrderController;
import com.game_trade.dao.OrderDao;
import com.game_trade.dao.UserDao;
import com.game_trade.domain.Commodity;
import com.game_trade.domain.Orders;
import com.game_trade.domain.User;
import com.game_trade.service.AdminService;
import com.game_trade.service.ICommodityService;
import com.game_trade.service.IOrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 翁鹏
 * @since 2023-04-12
 */
@Service
public class AdminServiceImpl extends ServiceImpl<UserDao, User> implements AdminService {


    @Autowired
    private ICommodityService commodityService;

    @Autowired
    private IOrdersService ordersService;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 审核通过
     * @param commodityId
     */
    @Override
    public void pass(Long commodityId) {
        Commodity commodity = commodityService.getById(commodityId);
        // 清除缓存
        String key = "commodity_" + commodity.getId();
        redisTemplate.delete(key);
        commodity.setStatus(Commodity.STATUS_AUDIT_SUCCESS);
        commodityService.updateById(commodity);
    }

    /**
     * 审核不通过
     * @param commodityId
     */
    @Override
    public void noPass(Long commodityId) {
        Commodity commodity = commodityService.getById(commodityId);
        // 清除缓存
        String key = "commodity_" + commodity.getId();
        redisTemplate.delete(key);
        commodity.setStatus(Commodity.STATUS_AUDIT_FAIL);
        commodityService.updateById(commodity);
    }


    @Override
    public PageResult<Orders> getBackList(int currentPage, int pageSize)
    {
        Page<Orders> pageOrders = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getStatus,Orders.STATUS_WAIT_BACK).or().eq(Orders::getStatus,Orders.STATUS_BAN_BACK).or().eq(Orders::getStatus,Orders.STATUS_FINISH_BACK);
        queryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(pageOrders,queryWrapper);
        PageResult<Orders> pageResult = new PageResult<>();
        pageResult.setList(pageOrders.getRecords());
        pageResult.setCurrent(pageOrders.getCurrent());
        pageResult.setPageSize(pageOrders.getSize());
        pageResult.setPages(pageOrders.getPages());
        pageResult.setTotal(pageOrders.getTotal());
        return pageResult;
    }

}

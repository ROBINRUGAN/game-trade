package com.game_trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.game_trade.common.PageResult;
import com.game_trade.domain.Orders;
import com.game_trade.domain.User;

import java.util.List;

public interface AdminService extends IService<User> {

    /**
     * 审核通过
     * @param commodityId
     * @return
     */
    void pass(Long commodityId);

    /**
     * 审核不通过
     * @param commodityId
     */
    void noPass(Long commodityId);

    /**
     * 获取退货商品列表
     * @param currentPage
     * @param pageSize
     * @return
     */
    PageResult<Orders> getBackList(int currentPage, int pageSize);
}

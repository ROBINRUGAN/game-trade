package com.game_trade.service;

import com.game_trade.common.PageResult;
import com.game_trade.domain.Complaints;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 翁鹏
 * @since 2023-04-12
 */
public interface IComplaintsService extends IService<Complaints> {

    /**
     * 分页查询投诉列表
     * @param page
     * @param size
     * @param type
     */
    PageResult<Complaints> pageComplaint(Integer page,Integer size,Integer type);
}

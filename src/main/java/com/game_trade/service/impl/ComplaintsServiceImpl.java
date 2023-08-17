package com.game_trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.game_trade.common.PageResult;
import com.game_trade.domain.Complaints;
import com.game_trade.dao.ComplaintsDao;
import com.game_trade.service.IComplaintsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 翁鹏
 * @since 2023-04-12
 */
@Service
public class ComplaintsServiceImpl extends ServiceImpl<ComplaintsDao, Complaints> implements IComplaintsService {

    /**
     * 分页查询投诉列表
     * @param page
     * @param size
     * @param type
     */
    @Override
    public PageResult<Complaints> pageComplaint(Integer page, Integer size, Integer type) {
        Page<Complaints> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Complaints> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Complaints::getType, type);
        queryWrapper.orderByDesc(Complaints::getUpdateTime);
        this.page(pageInfo, queryWrapper);
        PageResult<Complaints> pageResult = new PageResult<>();
        pageResult.setList(pageInfo.getRecords());
        pageResult.setCurrent(pageInfo.getCurrent());
        pageResult.setPageSize(pageInfo.getSize());
        pageResult.setPages(pageInfo.getPages());
        pageResult.setTotal(pageInfo.getTotal());
        return pageResult;
    }

}

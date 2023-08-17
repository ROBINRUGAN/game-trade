package com.game_trade.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.game_trade.common.PageResult;
import com.game_trade.domain.Commodity;
import com.game_trade.dao.CommodityDao;
import com.game_trade.dto.CommodityDto;
import com.game_trade.service.ICommodityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.game_trade.utils.OSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 翁鹏
 * @since 2023-04-01
 */
@Service
@Slf4j
public class CommodityServiceImpl extends ServiceImpl<CommodityDao, Commodity> implements ICommodityService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 上传商品信息
     *
     * @param commodityDto
     */
    @Override
    public void saveCommodity(CommodityDto commodityDto) throws IOException {
        // 将价格转化为BigDecimal类型
        //BigDecimal commodityPrice = new BigDecimal(price);
        // 得到当前登录用户的id，即为发布商品的用户id
        log.info("当前登录用户的id为：{}", StpUtil.getLoginIdAsLong());
        Long userId = StpUtil.getLoginIdAsLong();
        // 获得图片的url所凭借的字符串
        String images = OSSUtil.uploadFiles(commodityDto.getFiles());
        // 若商品id为空，则为新商品，获得商品的编号
        String commodityCode = UUID.randomUUID().toString().replaceAll("-", "");
        commodityDto.setCommodityCode(commodityCode);
        commodityDto.setSellerId(userId);
        commodityDto.setImages(images);
        // 保存商品信息
        this.save(commodityDto);
    }

    /**
     * 更新商品信息
     *
     * @param commodityDto
     */
    @Override
    public void updateCommodity(CommodityDto commodityDto) throws IOException {
        // 获得图片的url所凭借的字符串
        String images = OSSUtil.uploadFiles(commodityDto.getFiles());
        commodityDto.setImages(images);
        // 更新商品状态为待审核
        commodityDto.setStatus(0);
        this.updateById(commodityDto);
    }

    /**
     * 根据商品id逻辑删除商品
     *
     * @param id
     */
    @Override
    public void deleteCommodityById(Long id) {
        UpdateWrapper<Commodity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("is_deleted", Commodity.IS_DELETED_YES);
        this.update(updateWrapper);
    }


    /**
     * 分页查询商品信息
     *
     * @param page
     * @param size
     * @param status
     * @param keyword
     * @return
     */
    @Override
    public PageResult<Commodity> pageCommodity(Integer page, Integer size, Integer status, String keyword) {
        Page<Commodity> pageInfo = new Page<>(page, size);
        // 条件构造器
        LambdaQueryWrapper<Commodity> queryWrapper = new LambdaQueryWrapper<>();

        // 添加过滤条件,审核通过，未被删除，商品名称或者商品描述包含关键字
        queryWrapper.eq(Commodity::getStatus, status).eq(Commodity::getIsDeleted, Commodity.IS_DELETED_NO).and(
                wrapper -> wrapper.like(keyword != null, Commodity::getCommodityName, keyword)
                        .or().like(keyword != null, Commodity::getCommodityDescribe, keyword)
        );
        // 添加排序条件
        queryWrapper.orderByDesc(Commodity::getUpdateTime);
        // 执行分页查询
        this.page(pageInfo, queryWrapper);
        log.info(String.valueOf(pageInfo));
        // 封装分页数据
        PageResult<Commodity> pageResult = new PageResult<>();
        pageResult.setList(pageInfo.getRecords());
        pageResult.setCurrent(pageInfo.getCurrent());
        pageResult.setPageSize(pageInfo.getSize());
        pageResult.setPages(pageInfo.getPages());
        pageResult.setTotal(pageInfo.getTotal());

        return pageResult;
    }

    /**
     * 根据商品状态查询当前用户的商品信息
     *
     * @param status
     * @return
     */
    @Override
    public List<Commodity> queryUserCommodities(Integer status) {
        // 构造key
        Long userId = StpUtil.getLoginIdAsLong();
         // 从数据库中查询
        LambdaQueryWrapper<Commodity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Commodity::getSellerId, userId).eq(Commodity::getStatus, status).eq(Commodity::getIsDeleted, Commodity.IS_DELETED_NO);
        // 执行查询
        return this.list(queryWrapper);
    }


}

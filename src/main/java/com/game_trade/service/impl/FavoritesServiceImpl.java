package com.game_trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.game_trade.domain.Commodity;
import com.game_trade.domain.Favorites;
import com.game_trade.dao.FavoritesDao;
import com.game_trade.service.ICommodityService;
import com.game_trade.service.FavoritesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 翁鹏
 * @since 2023-04-06
 */
@Service
@Slf4j
public class FavoritesServiceImpl extends ServiceImpl<FavoritesDao, Favorites> implements FavoritesService {

    @Autowired
    private ICommodityService commodityService;

    /**
     * 取消收藏
     * @param favorites
     */
    @Override
    public void removeByUserIdAndCommodityId(Favorites favorites) {
        // 条件构造器
        LambdaQueryWrapper<Favorites> queryWrapper = new LambdaQueryWrapper<>();
        // 收藏者、收藏商品
        queryWrapper.eq(Favorites::getUserId, favorites.getUserId()).eq(Favorites::getCommodityId, favorites.getCommodityId());
        log.info("Favorites favorites:   "+ favorites.toString());
        // 删除收藏
        this.remove(queryWrapper);

    }

    /**
     * 判断是否收藏
     * @param favorites
     * @return
     */
    @Override
    public boolean isLikeByUserIdAndCommodityId(Favorites favorites) {
        return this.lambdaQuery().eq(Favorites::getUserId, favorites.getUserId())
                .eq(Favorites::getCommodityId, favorites.getCommodityId()).count() > 0;

    }

    /**
     * 根据用户id查询收藏商品
     * @param userId
     * @return
     */
    @Override
    public List<Commodity> listByUserId(Long userId) {
        // 条件构造器
        LambdaQueryWrapper<Favorites> queryWrapper = new LambdaQueryWrapper<>();
        // 收藏者
        queryWrapper.eq(Favorites::getUserId, userId);
        // 查询收藏
        List<Favorites> favorites = this.list(queryWrapper);
        // 收藏商品id
        List<Long> commodityIds = favorites.stream().map(Favorites::getCommodityId).collect(Collectors.toList());
        // 查询收藏商品
        return commodityService.listByIds(commodityIds);
    }
}

package com.game_trade.service;

import com.game_trade.domain.Commodity;
import com.game_trade.domain.Favorites;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 翁鹏
 * @since 2023-04-06
 */
public interface FavoritesService extends IService<Favorites> {

    /**
     * 取消收藏
     * @param favorites
     */
    void removeByUserIdAndCommodityId(Favorites favorites);

    /**
     * 判断是否收藏
     * @param favorites
     * @return
     */
    boolean isLikeByUserIdAndCommodityId(Favorites favorites);

    /**
     * 跟据用户id查询收藏商品
     * @param userId
     * @return
     */
    List<Commodity> listByUserId(Long userId);
}

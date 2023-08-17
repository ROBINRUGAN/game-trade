package com.game_trade.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.game_trade.domain.Favorites;
import com.game_trade.domain.User;
import com.game_trade.service.FavoritesService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 翁鹏
 * @since 2023-04-06
 */
@RestController
@RequestMapping("/favorites")
@Slf4j
@SaCheckRole(value = {User.ROLE_ADMIN, User.ROLE_WHITE} , mode = SaMode.OR)
public class FavoritesController {

    @Autowired
    private FavoritesService favoritesService;

    /**
     * 收藏商品
     * @param commodityId
     * @return
     */
    @PostMapping
    @ApiOperation(value = "收藏商品接口")
    public SaResult favorites(Long commodityId){
        //获取当前用户
        Long userId = StpUtil.getLoginIdAsLong();
        //判断是否已经收藏
        Favorites fav = favoritesService.getOne(new LambdaQueryWrapper<Favorites>()
                .eq(Favorites::getUserId, userId).eq(Favorites::getCommodityId, commodityId));
        if (fav != null){
            return SaResult.error("已收藏");
        }
        //收藏商品
        Favorites favorites = new Favorites();
        favorites.setUserId(userId);
        favorites.setCommodityId(commodityId);
        favoritesService.save(favorites);
        return SaResult.ok("收藏成功");
    }

    /**
     * 取消收藏
     * @param commodityId
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "取消收藏接口")
    public SaResult cancel(Long commodityId){
        log.info("commodityId:   "+ commodityId);
        //获取当前用户
        Long userId = StpUtil.getLoginIdAsLong();
        // 封装
        Favorites favorites = new Favorites();
        favorites.setUserId(userId);
        favorites.setCommodityId(commodityId);
        //取消收藏
        favoritesService.removeByUserIdAndCommodityId(favorites);
        return SaResult.ok("取消收藏成功");
    }

    /**
     * 根据用户id判断是否收藏某商品
     * @param commodityId
     * @return
     */
    @GetMapping("/isFavorite")
    @ApiOperation(value = "根据用户id判断是否收藏某商品")
    public SaResult isLike(Long commodityId){
        Long userId = StpUtil.getLoginIdAsLong();
        // 封装
        Favorites favorites = new Favorites();
        favorites.setUserId(userId);
        favorites.setCommodityId(commodityId);
        // 判断
        boolean isLike = favoritesService.isLikeByUserIdAndCommodityId(favorites);
        return SaResult.data(isLike);

    }

    /**
     * 查询当前用户收藏的商品
     * @return
     */
    @GetMapping
    @ApiOperation(value = "查询当前用户收藏的商品")
    public SaResult list(){
        Long userId = StpUtil.getLoginIdAsLong();
        return SaResult.data(favoritesService.listByUserId(userId));
    }


}


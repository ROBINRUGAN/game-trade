package com.game_trade.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.util.SaResult;
import com.game_trade.common.Code;
import com.game_trade.common.PageResult;
import com.game_trade.domain.Commodity;
import com.game_trade.domain.User;
import com.game_trade.dto.CommodityDto;
import com.game_trade.service.ICommodityService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 翁鹏
 * @since 2023-04-01
 */
@RestController
@RequestMapping("/commodity")
@Slf4j
@SaCheckRole(value = {User.ROLE_ADMIN, User.ROLE_WHITE} , mode = SaMode.OR)
public class CommodityController {

    @Autowired
    private ICommodityService commodityService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 上传图片
     *
     * @param commodityDto
     * @return
     * @throws IOException
     */
    @PostMapping
    @ApiOperation("上传商品信息")
    public SaResult save(CommodityDto commodityDto) throws IOException {
        if (StringUtils.isBlank(commodityDto.getCommodityName())) {
            return SaResult.error("商品名称不能为空");
        }
        if (StringUtils.isBlank(commodityDto.getCommodityDescribe())) {
            return SaResult.error("商品描述不能为空");
        }
        if (StringUtils.isBlank(commodityDto.getPrice().toString())) {
            return SaResult.error("商品价格不能为空");
        }
        if (commodityDto.getFiles().length == 0) {
            return SaResult.error("商品图片不能为空");
        }
        log.info(commodityDto.toString());
        commodityService.saveCommodity(commodityDto);
        return SaResult.ok("上传成功");

    }

    /**
     * 查询商品信息
     *
     * @return
     */
    @GetMapping
    @ApiOperation("查询商品信息")
    public SaResult page(@RequestParam(required = false, defaultValue = "1") int currentPage, @RequestParam(required = false, defaultValue = "10") int pageSize, String commodityKey) {
        // 从缓存中获取数据

        PageResult pageResult = commodityService.pageCommodity(currentPage, pageSize,Commodity.STATUS_AUDIT_SUCCESS , commodityKey);
        return SaResult.get(Code.OK, "查询成功", pageResult);
    }

    /**
     * 修改商品信息
     *
     * @param commodityDto
     * @return
     */
    @PutMapping
    @ApiOperation("修改商品信息")
    public SaResult update(CommodityDto commodityDto) throws IOException {
        if (StringUtils.isBlank(commodityDto.getCommodityName())) {
            return SaResult.error("商品名称不能为空");
        }
        if (StringUtils.isBlank(commodityDto.getCommodityDescribe())) {
            return SaResult.error("商品描述不能为空");
        }
        if (StringUtils.isBlank(commodityDto.getPrice().toString())) {
            return SaResult.error("商品价格不能为空");
        }
        if (commodityDto.getFiles().length == 0) {
            return SaResult.error("商品图片不能为空");
        }
        log.info(commodityDto.toString());
        // 清除缓存
        String key = "commodity_" + commodityDto.getId();
        redisTemplate.delete(key);
        // 更新商品信息
        commodityService.updateCommodity(commodityDto);
        return SaResult.ok("更新成功");
    }

    /**
     * 删除商品信息
     *
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除商品信息")
    public SaResult delete(@RequestParam("id") Long id) {
        // 清除缓存
        String key = "commodity_" + id;
        redisTemplate.delete(key);
        commodityService.deleteCommodityById(id);
        return SaResult.ok("删除成功");
    }

    /**
     * 根据id查询商品信息
     *
     * @return
     */
    @GetMapping("/id")
    @ApiOperation("根据id查询商品信息")
    public SaResult getCommodityById(@RequestParam("id") Long id) {
        //动态构造key
        String key = "commodity_" + id;
        ////先从redis中获取缓存数据
        Commodity commodity = (Commodity) redisTemplate.opsForValue().get(key);
        if (commodity != null) {
            return SaResult.get(Code.OK, "查询成功", commodity);
        }
        //如果redis中没有数据，就从数据库中获取数据
        commodity = commodityService.getById(id);
        //将数据存入redis中
        redisTemplate.opsForValue().set(key, commodity,60, TimeUnit.MINUTES);
        return SaResult.get(Code.OK, "查询成功", commodity);
    }


/*    *//**
     * 删除商品的图片
     *
     * @param url
     * @return
     *//*
    @PostMapping("/delete")
    public SaResult delete(@RequestParam("url") String url) {
        OSSUtil ossUtil = new OSSUtil();
        ossUtil.deleteImg(url);
        return SaResult.ok();
    }*/

}


package com.game_trade.service;

import com.game_trade.common.PageResult;
import com.game_trade.domain.Commodity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.game_trade.dto.CommodityDto;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 翁鹏
 * @since 2023-04-01
 */
public interface ICommodityService extends IService<Commodity> {

    /**
     * 上传商品信息
     * @param commodityDto
     * @throws IOException
     */
    void saveCommodity(CommodityDto commodityDto) throws IOException;

    /**
     * 修改商品信息
     * @param commodityDto
     * @throws IOException
     */
    void updateCommodity(CommodityDto commodityDto) throws IOException;

    /**
     * 逻辑删除商品信息
     * @param id
     */
    void deleteCommodityById(Long id);

    /**
     * 分页查询商品信息
     * @param page
     * @param size
     * @param status
     * @param keyword
     * @return
     */
     PageResult<Commodity> pageCommodity(Integer page, Integer size, Integer status, String keyword);

    /**
     * 查询用户的商品信息
     * @param status
     * @return
     */
    List<Commodity> queryUserCommodities(Integer status);


}

package com.game_trade.domain;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author 翁鹏
 * @since 2023-04-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Favorites implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏者
     */
    private Long userId;

    /**
     * 收藏商品
     */
    private Long commodityId;


}

package com.game_trade.dto;

import com.game_trade.common.GameAccountInfo;
import lombok.Data;

/**
 * @author 翁鹏
 */
@Data
public class GameAccountInfoDto extends GameAccountInfo {
    /**
     * 对应的订单id
     */
    private Long orderId;
}

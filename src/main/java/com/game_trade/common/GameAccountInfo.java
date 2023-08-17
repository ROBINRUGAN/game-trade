package com.game_trade.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameAccountInfo {
    /**
     * 游戏账号
     */
    private String gameAccount;
    /**
     * 游戏密码
     */
    private String gamePassword;
    /**
     * 备注（游戏区服等）
     */
    private String remark;

}

package com.game_trade.dto;

import com.game_trade.domain.User;
import lombok.Data;

/**
 * @author 翁鹏
 */
@Data
public class UserDto extends User {
    /**
     * 验证码
     */
    private String code;
}

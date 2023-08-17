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
 * @since 2023-05-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ChatBlock implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前用户id
     */
    private Long userId;

    /**
     * 屏蔽人id
     */
    private Long blockedId;


}

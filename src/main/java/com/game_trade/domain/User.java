package com.game_trade.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author 翁鹏
 * @since 2023-03-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    // 管理员
    public static final String ROLE_ADMIN = "admin";
    // 白名单
    public static final String ROLE_WHITE = "white";
    // 黑名单
    public static final String ROLE_BLACK = "black";


//    // 转化为字符串，防止精度丢失
//    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    //@TableField("user_name")
    private String username;

    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 权限
     */
    private String permission;

    /**
     * 角色
     */
    private String role;

    /**
     * 是否实名认证 0未实名 1实名
     */
    private Boolean certification;


}

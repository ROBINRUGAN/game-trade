package com.game_trade.service;

import com.game_trade.common.BaseUserInfoResult;
import com.game_trade.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 翁鹏
 * @since 2023-03-27
 */
public interface IUserService extends IService<User> {

    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    public User queryUserByName(String username);

    /**
     * 发送邮件
     * @param email
     * @param code
     */
    public void sendEmail(String email,String code);

    /**
     * 判断邮箱是否存在
     * @param email
     * @return
     */
    public boolean checkEmail(String email);

    /**
     * 判断验证码是否正确
     * @param email
     * @param code
     * @return
     */
    public boolean checkCode(String email,String code);

    /**
     * 获取用户角色
     * @param username
     * @return
     */
    public String getUserRole(String username);


    /**
     * 判断用户名是否存在
     * @param username
     * @return
     */
    boolean checkUsername(String username);

    /**
     * 判断用户是否实名认证
     * @return
     */
    boolean isCertification();

    /**
     * 根据用户id获取用户基本信息
     * @param userId
     * @return
     */
    BaseUserInfoResult getUserInfoById(Long userId);

    /**
     * 根据用户名获取用户基本信息
     * @param username
     * @return
     */
    BaseUserInfoResult getUserInfoByUsername(String username);
}

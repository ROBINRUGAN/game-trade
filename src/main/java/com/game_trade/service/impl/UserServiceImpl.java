package com.game_trade.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.game_trade.common.BaseUserInfoResult;
import com.game_trade.domain.User;
import com.game_trade.dao.UserDao;
import com.game_trade.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 翁鹏
 * @since 2023-03-27
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements IUserService {

    //	引入邮件接口
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IUserService userService;


    //根据用户名查询用户
    @Override
    public User queryUserByName(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return this.getOne(queryWrapper);
    }

    //发送邮件
    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendEmail(String email, String code) {
        //创建邮件消息
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("验证码");
        message.setText("验证码为：" + code + " 五分钟之内有效\n" +
                "如非本人操作，请忽略本邮件。");
        mailSender.send(message);
    }

    //判断邮箱是否存在
    @Override
    public boolean checkEmail(String email) {
        //数据库搜索 mybatis+的好处
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        User user = userService.getOne(queryWrapper);
        return user != null;
    }

    //校验验证码
    @Override
    public boolean checkCode(String email, String code) {
        //从Redis中获取缓存的验证码
        Object codeInSession = redisTemplate.opsForValue().get(email);
        //进行验证码的比对（页面提交的验证码和保存的验证码比对）
        if (codeInSession != null && codeInSession.equals(code)) {
            //如果校验成功，删除Redis中缓存的验证码
            redisTemplate.delete(email);
            return true;
        }
        return false;
    }

    //查询用户角色
    @Override
    public String getUserRole(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userService.getOne(queryWrapper);
        return user.getRole();
    }


    //判断用户名是否存在
    @Override
    public boolean checkUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userService.getOne(queryWrapper);
        return user != null;
    }

    //判断用户是否实名认证
    @Override
    public boolean isCertification() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        return  user.getCertification();
    }

    /**
     * 根据用户id获取用户基本信息
     * @param userId
     * @return
     */
    @Override
    public BaseUserInfoResult getUserInfoById(Long userId) {
        // 根据用户id获取用户信息
        User user = userService.getById(userId);
        BaseUserInfoResult baseUserInfoResult = new BaseUserInfoResult();
        // 将用户信息封装到BaseUserInfoResult中
        baseUserInfoResult.setId(user.getId());
        baseUserInfoResult.setUsername(user.getUsername());
        baseUserInfoResult.setNickname(user.getNickname());
        baseUserInfoResult.setAvatar(user.getAvatar());
        baseUserInfoResult.setCertification(user.getCertification());
        baseUserInfoResult.setRole(user.getRole());
        return baseUserInfoResult;
    }

    @Override
    public BaseUserInfoResult getUserInfoByUsername(String username) {
        // 根据用户名获取用户信息
        User user = userService.queryUserByName(username);
        BaseUserInfoResult baseUserInfoResult = new BaseUserInfoResult();
        // 将用户信息封装到BaseUserInfoResult中
        baseUserInfoResult.setId(user.getId());
        baseUserInfoResult.setUsername(user.getUsername());
        baseUserInfoResult.setNickname(user.getNickname());
        baseUserInfoResult.setAvatar(user.getAvatar());
        baseUserInfoResult.setCertification(user.getCertification());
        baseUserInfoResult.setRole(user.getRole());
        return baseUserInfoResult;
    }
}

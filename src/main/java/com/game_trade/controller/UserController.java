package com.game_trade.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.game_trade.common.BaseContext;
import com.game_trade.common.BaseUserInfoResult;
import com.game_trade.common.Code;
import com.game_trade.domain.Commodity;
import com.game_trade.dto.UserDto;
import com.game_trade.domain.User;
import com.game_trade.service.IAccountService;
import com.game_trade.service.ICommodityService;
import com.game_trade.service.IUserService;
import com.game_trade.utils.CertifiUtil;
import com.game_trade.utils.MD5Util;
import com.game_trade.utils.OSSUtil;
import com.game_trade.utils.ValidateCodeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 翁鹏
 * @since 2023-03-27
 */
@RestController
@RequestMapping("/user")
@Slf4j
@Api(tags = "用户相关接口")
@SaCheckRole(value = {User.ROLE_ADMIN, User.ROLE_WHITE} , mode = SaMode.OR)
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private ICommodityService commodityService;

    /**
     * 发送邮箱验证码
     * <br/>
     * 前端post数据只有邮箱一个字段，
     * 通过映射到User数据类中，可以调用对应的getter，提升复用性
     * @param user
     * @return
     */
    @PostMapping("/sendCode")
    @ApiOperation(value = "发送邮箱验证码接口")
    @SaIgnore
    public SaResult sendMsg(@RequestBody User user) {
        //获取邮箱
        String email = user.getEmail();
        if (userService.checkEmail(email)) {
            return SaResult.error("邮箱已被注册");
        }
        if (StringUtils.isNotEmpty(email)) {
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            //调用API完成发送验证码
            userService.sendEmail(email, code);
            log.info("code={}", code);
            //将生成的验证码缓存到Redis中，并且设置有效期为5分钟
            //Radis以键值对的形式存储验证码，以便于后面的注册接口进行比对
            redisTemplate.opsForValue().set(email, code, 5, TimeUnit.MINUTES);
            return SaResult.ok("验证码发送成功");
        }
        return SaResult.error("验证码发送失败");
    }

    /**
     * 注册
     * userDto是用来处理验证码的，通过继承补充一个code属性
     * 自带getter setter
     * @param userDto
     * @return
     */
    @PostMapping("/register")
    @ApiOperation(value = "注册接口")
    @SaIgnore
    public SaResult register(@RequestBody UserDto userDto) {
        log.info("register={}", userDto.toString());
        //判断参数不为空
        if (userDto.getUsername() == null && userDto.getPassword() == null && userDto.getEmail() == null && userDto.getCode() == null) {
            return SaResult.error("参数不能为空");
        }
        // 判断用户名是否存在
        if (userService.checkUsername(userDto.getUsername())) {
            return SaResult.error("用户名已存在");
        }
        //获取邮箱
        String email = userDto.getEmail();
        //获取验证码
        String code = userDto.getCode();
        // 判断邮箱是否存在
        if (userService.checkEmail(email)) {
            return SaResult.error("邮箱已被注册");
        }
        // 判断密码是否大于6位
        if (userDto.getPassword().length() < 6) {
            return SaResult.error("密码不能小于6位");
        }
        //判断验证码是否正确
        if (userService.checkCode(email, code)) {
            //对密码进行加密
            userDto.setPassword(MD5Util.getMD5WithSalt(userDto.getPassword()));
            userDto.setCertification(false);
            //将用户信息保存到数据库中
            userService.save(userDto);
            return SaResult.ok("注册成功");
        }
        return SaResult.error("验证码错误");
    }


    /**
     * 登录
     *
     * @param user
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "登录接口")
    @SaIgnore
    public SaResult login(@RequestBody User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        Map<String, Object> map = new HashMap<>();
        User queryUser = userService.queryUserByName(username);
        if (queryUser == null) {
            return SaResult.error("用户名不正确");
        }
        if (!queryUser.getPassword().equals(MD5Util.getMD5WithSalt(password))) {
            return SaResult.error("密码不正确");
        }
        //根据id，进行登录
        StpUtil.login(queryUser.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        map.put("username", username);
        map.put("token", tokenInfo.getTokenValue());
        // 得到用户id
        Long userId = StpUtil.getLoginIdAsLong();
        // 设置当前登录用户id
        BaseContext.setCurrentId(userId);
        return SaResult.get(Code.OK, "登录成功", map);
    }

    /**
     * 退出登录
     *
     * @return
     */
    @RequestMapping("logout")
    @ApiOperation(value = "退出登录接口")
    @SaIgnore
    public SaResult logout() {
        StpUtil.logout();
        return SaResult.ok();
    }

    /**
     * 测试跨域
     *
     */
    @GetMapping("/test")
    @ApiOperation(value = "测试跨域接口")
    public SaResult test() {
        return SaResult.ok("测试跨域成功");
    }


    /**
     * 获取用户信息
     *
     * @return
     */
    @GetMapping
    @ApiOperation(value = "获取用户信息接口")
    @SaIgnore
    public SaResult getUserInfo() {
        // 动态构造key，用来缓存
        String key = "user_" + StpUtil.getLoginIdAsLong();
        // 从缓存中获取用户信息
        User user = (User) redisTemplate.opsForValue().get(key);
        if (user == null) {
            // 缓存中没有，从数据库中获取
            user = userService.getById(StpUtil.getLoginIdAsLong());
            // 将用户信息存入缓存中
            redisTemplate.opsForValue().set(key, user,60, TimeUnit.MINUTES);
        }
        // 将密码设置为空
        user.setPassword(null);
        return SaResult.get(Code.OK, "获取用户信息成功", user);
    }

    /**
     * 更改用户头像
     *
     * @param avatar
     * @return
     */
    @PutMapping("avatar")
    @ApiOperation(value = "更改用户头像接口")
    public SaResult updateAvatar(MultipartFile avatar) throws IOException {
        String key = "user_" + StpUtil.getLoginIdAsLong();
        // 清除缓存
        redisTemplate.delete(key);
        User user = userService.getById(StpUtil.getLoginIdAsLong());
        //String oldAvatar = user.getAvatar();
        String avatarUrl = OSSUtil.uploadFile(avatar);
        user.setAvatar(avatarUrl);
        userService.updateById(user);
        return SaResult.get(Code.OK, "更改用户头像成功", avatarUrl);
    }

    /**
     * 更改用户昵称
     *
     * @param username
     * @return
     */
    @PutMapping("username")
    @ApiOperation(value = "更改用户昵称接口")
    public SaResult updateUserName(String username) {
        if (StringUtils.isEmpty(username)) {
            return SaResult.error("昵称不能为空");
        }
        if (userService.checkUsername(username)) {
            return SaResult.error("昵称已存在");
        }
        String key = "user_" + StpUtil.getLoginIdAsLong();
        // 清除缓存
        redisTemplate.delete(key);
        User user = userService.getById(StpUtil.getLoginIdAsLong());
        user.setUsername(username);
        userService.updateById(user);
        return SaResult.get(Code.OK, "更改用户昵称成功", username);
    }

    /**
     * 更改用户密码
     *
     * @return
     */
    @PutMapping("password")
    @ApiOperation(value = "更改用户密码接口")
    public SaResult updatePassword(String oldPassword, String newPassword) {
        if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword)) {
            return SaResult.error("密码不能为空");
        }
        User user = userService.getById(StpUtil.getLoginIdAsLong());
        if (!user.getPassword().equals(MD5Util.getMD5WithSalt(oldPassword))) {
            return SaResult.error("原密码不正确");
        }else if (newPassword.length() < 6) {
            return SaResult.error("密码长度不能小于6位");
        }
        // 判断新密码是否与原密码相同
        if (user.getPassword().equals(MD5Util.getMD5WithSalt(newPassword))) {
            return SaResult.error("新密码不能与原密码相同");
        }
        user.setPassword(MD5Util.getMD5WithSalt(newPassword));
        userService.updateById(user);
        return SaResult.get(Code.OK, "更改用户密码成功", null);
    }

    /**
     * 查询用户是否实名认证
     * @return
     */
    @GetMapping("certificated")
    @ApiOperation(value = "查询用户是否实名认证接口")
    @SaIgnore
    public SaResult isCertificated() {
        if (userService.isCertification()) {
            return SaResult.get(Code.OK, "已实名认证", true);
        }
        return SaResult.get(Code.OK, "未实名认证", false);
    }

    /**
     * 实名认证
     * @param realName
     * @param idCard
     * @return
     */
    @PutMapping("certifi")
    @ApiOperation(value = "实名认证接口")
    public SaResult certifi(String realName, String idCard) throws  IOException {
        // 用户已经实名认证
        if (userService.isCertification()) {
            return SaResult.ok("已实名认证");
        }
        if (StringUtils.isEmpty(realName) || StringUtils.isEmpty(idCard)) {
            return SaResult.error("参数不能为空");
        }
        Map<String, String> result = CertifiUtil.checkCertifi(realName, idCard);
        if (result!= null && CertifiUtil.SUCCESS.equals(result.get(CertifiUtil.RES))) {
            String key = "user_" + StpUtil.getLoginIdAsLong();
            // 清除缓存
            redisTemplate.delete(key);
            User user = userService.getById(StpUtil.getLoginIdAsLong());
            user.setCertification(true);
            userService.updateById(user);
            return SaResult.get(Code.OK, "实名认证成功", result);
        }
        return SaResult.get(Code.ERR, "实名认证失败", result);
    }

    /**
     * 根据用户id查询用户基本信息
     * @param userId
     * @return
     */
    @GetMapping("userId")
    @ApiOperation(value = "根据用户id查询用户基本信息接口")
    @SaIgnore
    public SaResult getUserInfoById(@RequestParam("userId") Long userId) {
        String key = "Base::UserId_" + StpUtil.getLoginIdAsLong();
        // 从缓存中获取用户信息
        BaseUserInfoResult user = (BaseUserInfoResult) redisTemplate.opsForValue().get(key);
        if (user == null) {
            // 缓存中没有，从数据库中获取
            user = userService.getUserInfoById(userId);
            // 将用户信息存入缓存中
            redisTemplate.opsForValue().set(key, user,60, TimeUnit.MINUTES);
        }
        return SaResult.get(Code.OK, "查询成功", user);
    }

    /**
     * 根据用户名查询用户基本信息
     * @param username
     * @return
     */
    @GetMapping("username")
    @ApiOperation(value = "根据用户名查询用户基本信息接口")
    public SaResult getUserInfoByUsername(@RequestParam("username") String username) {

        BaseUserInfoResult user = userService.getUserInfoByUsername(username);
        if (user == null) {
            return SaResult.error("用户不存在");
        }
        return SaResult.get(Code.OK, "查询成功", user);
    }


    /**
     * 查询当前用户审核成功的商品
     * @return
     */
    @GetMapping("passedCommodities")
    @ApiOperation(value = "查询当前用户审核成功的商品接口")
    public SaResult passedCommodities() {
        List<Commodity> commodities = commodityService.queryUserCommodities(Commodity.STATUS_AUDIT_SUCCESS);
        return SaResult.get(Code.OK, "查询成功", commodities);
    }

    /**
     * 查询当前用户审核失败的商品
     * @return
     */
    @GetMapping("failedCommodities")
    @ApiOperation(value = "查询当前用户审核失败的商品接口")
    public SaResult failedCommodities() {
        List<Commodity> commodities = commodityService.queryUserCommodities(Commodity.STATUS_AUDIT_FAIL);
        return SaResult.get(Code.OK, "查询成功", commodities);
    }

    /**
     * 查询当前用户审核中的商品
     * @return
     */
    @GetMapping("auditingCommodities")
    @ApiOperation(value = "查询当前用户审核中的商品接口")
    public SaResult auditingCommodities() {
        List<Commodity> commodities = commodityService.queryUserCommodities(Commodity.STATUS_WAIT_AUDIT);
        return SaResult.get(Code.OK, "查询成功", commodities);
    }

    /**
     * 查询当前用户已经卖出的商品
     * @return
     */
    @GetMapping("soldCommodities")
    @ApiOperation(value = "查询当前用户已经卖出的商品接口")
    public SaResult soldCommodities() {
        List<Commodity> commodities = commodityService.queryUserCommodities(Commodity.STATUS_SOLD_OUT);
        return SaResult.get(Code.OK, "查询成功", commodities);
    }



}


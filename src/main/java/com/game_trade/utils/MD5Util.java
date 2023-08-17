package com.game_trade.utils;


import cn.hutool.crypto.SecureUtil;

public class MD5Util {
    //盐值
    private static final String salt = "secret";

    //迭代次数
    private static final int hashIterations = 12;

    //获得盐值
    public static String getSalt() {
        return salt;
    }
    //获取迭代次数
    public static int getHashIterations() {
        return hashIterations;
    }

    //加盐12次迭代加密
    public static String getMD5WithSalt(String password) {
        // MD5加盐加密
        String md5Hex = SecureUtil.md5(password + salt);
        // 1024次加密
        for (int i = 0; i < hashIterations; i++) {
            md5Hex = SecureUtil.md5(md5Hex);
        }
        return md5Hex;
    }

}

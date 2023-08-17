package com.game_trade.component;

import cn.dev33.satoken.stp.StpInterface;
import com.game_trade.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class StpInterfaceImpl implements StpInterface {
    @Autowired
    IUserService userService;


    @Override
    public List<String> getPermissionList(Object o, String s) {
        return null;
    }

    /**
     * 获得用户角色，不过用户只有一个角色，使用列表是因为调用现有接口
     * 提供了两个东西，一个是角色类型，一个是许可证
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        log.info("-------------------loginId:{}-----------------------",loginId);
        List<String> list = new ArrayList<String>();
        Long id = Long.parseLong(loginId.toString());
        list.add(userService.getUserRole(userService.getById(id).getUsername()));
        return list;
    }
}

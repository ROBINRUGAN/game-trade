package com.game_trade.controller;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.game_trade.common.BaseUserInfoResult;
import com.game_trade.service.IChatLinksService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 翁鹏
 * @since 2023-05-09
 */
@RestController
@RequestMapping("/chatLinks")
public class ChatLinksController {
    @Autowired
    private IChatLinksService chatLinksService;

    /**
     * 获得当前用户的所有聊天关系，即对话框
     * @return
     */
    @GetMapping("/getChatLinks")
    @ApiOperation("获得当前用户的所有聊天关系，即对话框")
    public SaResult getChatLinks() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<BaseUserInfoResult> results = chatLinksService.selectChatLinks(userId);
        return SaResult.data(results);
    }

}


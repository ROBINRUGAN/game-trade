package com.game_trade.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.game_trade.domain.Complaints;
import com.game_trade.domain.User;
import com.game_trade.service.IComplaintsService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 翁鹏 吴荣榜
 * @since 2023-04-12
 */
@RestController
@RequestMapping("/complaints")
@Slf4j
@SaCheckRole(value = {User.ROLE_ADMIN, User.ROLE_WHITE} , mode = SaMode.OR)
public class ComplaintsController {

    @Autowired
    private IComplaintsService complaintsService;

    /**
     * 用户举报商品
     * @param complaints
     * @return
     */
    @PostMapping("/item")
    @ApiOperation("用户举报商品")
    public SaResult complaintsItem(@RequestBody Complaints complaints){
        log.info("举报数据：{}",complaints);
        Long userId = StpUtil.getLoginIdAsLong();
        complaints.setUserId(userId);
        //0为举报卖家 1为举报订单
        complaints.setType(1);
        complaintsService.save(complaints);
        return SaResult.ok("已提交用户举报商品信息");
    }

    /**
     * 用户举报卖家
     * @param complaints
     * @return
     */
    @PostMapping("/seller")
    @ApiOperation("用户举报卖家")
    public SaResult complaintsSeller(@RequestBody Complaints complaints){
        log.info("举报数据：{}",complaints);
        Long userId = StpUtil.getLoginIdAsLong();
        complaints.setUserId(userId);
        complaints.setType(0);
        complaintsService.save(complaints);
        return SaResult.ok("已提交用户举报卖家信息");
    }

}


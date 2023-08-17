package com.game_trade.common;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.util.SaResult;
import com.game_trade.exception.BusinessException;
import com.game_trade.exception.CustomException;
import com.game_trade.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */

//加了这两个注解的controller都会被拦截
@ControllerAdvice(annotations = {RestController.class,Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 其他的奇奇怪怪的抛错
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public SaResult exceptionHandler(CustomException ex){
        log.error(ex.getMessage());

        return SaResult.error("未知错误");
    }

    /**
     * satoken异常处理方法
     * @param e
     * @return
     */
    @ExceptionHandler
    public SaResult handlerException(Exception e) {
        e.printStackTrace();
        if (e instanceof NotLoginException) {
            return SaResult.error("未登录");
        }
        if (e instanceof NotRoleException || e instanceof NotPermissionException) {
            return SaResult.error("无权限");
        }
        if (e instanceof DisableServiceException) {
            return SaResult.error("当前账号服务已被封禁");
        }
        if(e instanceof SQLIntegrityConstraintViolationException ){
            log.error(e.getMessage());
            if (e.getMessage().contains("Duplicate entry")){
                String[] split =e.getMessage().split(" ");
                String msg= split[2]+"已存在";
                return SaResult.error(msg);
            }
        }
        return SaResult.error("未知错误");
    }

    @ExceptionHandler(Exception.class)
    public SaResult doExpetion(Exception ex){
        //记录日志(错误堆栈)
        //发送消息给运维
        //发送邮件给开发人员，exception对象发送给开发人员
        log.error("其他异常被捕获"+ex.getMessage());
        return SaResult.error("系统异常");   }

    @ExceptionHandler(SystemException.class)
    public SaResult doSystemException(SystemException exception) {
        //记录日志(错误堆栈)
        //发送消息给运维
        //发送邮件给开发人员，exception对象发送给开发人员
        log.error("系统异常被捕获"+exception.getMessage());
        return SaResult.error(exception.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public SaResult doSystemException(BusinessException exception) {
        log.error("业务异常被捕获"+exception.getMessage());
        return SaResult.error(exception.getMessage());
    }
}

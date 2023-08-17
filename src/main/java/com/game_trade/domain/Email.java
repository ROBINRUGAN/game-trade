package com.game_trade.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 翁鹏
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Email implements Serializable {
    /**
     *  收件人
     */
    private String[] tos;
    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 邮件内容
     */
    private String content;
}


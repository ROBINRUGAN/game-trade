package com.game_trade.domain;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author 翁鹏
 * @since 2023-05-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    // 信息类型为图片
    public static final Integer TYPE_IMG = 1;
    // 信息类型为文字
    public static final Integer TYPE_TEXT = 0;
    // 信息类型为系统
    public static final Integer TYPE_SYSTEM = 2;
    // 信息未读
    public static final Boolean READ_FALSE = false;
    // 信息已读
    public static final Boolean READ_TRUE = true;

    /**
     * 消息主键
     */
    private Long id;

    /**
     * 发送者id
     */
    private Long senderId;

    /**
     * 接收者id
     */
    private Long recipientId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型 0文字 1图片 2系统
     */
    private Integer type;

    /**
     * 创建时间
     */
    //@TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 是否已读 0未读 1已读
     */
    private Boolean isRead;


}

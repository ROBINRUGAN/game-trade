package com.game_trade.domain;

import java.math.BigDecimal;
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
 * @since 2023-04-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Commodity implements Serializable {

    // 状态
    // 0审核中 1审核成功 2审核失败 3已售出
    public static final Integer STATUS_WAIT_AUDIT = 0;
    public static final Integer STATUS_AUDIT_SUCCESS = 1;
    public static final Integer STATUS_AUDIT_FAIL = 2;
    public static final Integer STATUS_SOLD_OUT = 3;

    // 是否删除
    // 0未删除 1删除
    public static final Integer IS_DELETED_NO = 0;
    public static final Integer IS_DELETED_YES = 1;
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 卖家id
     */
    private Long sellerId;

    /**
     * 商品名称
     */
    private String commodityName;

    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 商品描述
     */
    private String commodityDescribe;

    /**
     * 商品价格
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal price;

    /**
     * 商品码
     */
    private String commodityCode;

    /**
     * 图片
     */
    private String images;

    /**
     * 状态 0审核中 1审核成功 2审核失败 3已售出
     */
    private Integer status;

    /**
     * 是否删除 0未删除 1删除
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;



}

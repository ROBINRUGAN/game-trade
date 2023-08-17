package com.game_trade.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author 翁鹏
 * @since 2023-04-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Orders implements Serializable {
    // 状态
    // 1待支付 2已支付 3已完成 4已取消
    // 5订单结束(用户确认收货)
    // 6退货待审核 7退货已拒绝 8退货已完成
    public static final Integer STATUS_WAIT_PAY = 1;
    public static final Integer STATUS_PAID = 2;
    public static final Integer STATUS_COMPLETE = 3;
    public static final Integer STATUS_CANCEL = 4;

    public static final Integer STATUS_FINISH = 5;

    public static final Integer STATUS_WAIT_BACK = 6;

    public static final Integer STATUS_BAN_BACK = 7;

    public static final Integer STATUS_FINISH_BACK = 8;

    public static final Integer REFUSE_BACK = 100;
    public static final Integer SLIGHT_DAMAGE = 101;
    public static final Integer SERIOUS_DAMAGE = 102;
    public static final Integer NO_DAMAGE = 103;

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Long id;

    /**
     * 订单号
     */
    private String number;

    /**
     * 商品账号id
     */
    private Long commodityId;

    /**
     * 订单状态 1待支付 2已支付 3已完成 4已取消
     */
    private Integer status;

    /**
     * 卖家id
     */
    private Long sellerId;

    /**
     * 下单用户id
     */
    private Long buyerId;

    /**
     * 下单时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime orderTime;

    /**
     * 完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime checkoutTime;

    /**
     * 实际金额
     */
    private BigDecimal amount;

    /**
     * 备注
     */
    private String remark;


}

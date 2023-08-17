package com.game_trade.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {
    /**
     * 当前页
     */
    private Long current;

    /**
     * 总条数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 每页条数
     */
    private Long pageSize;

    /**
     * 数据
     */
    private List<T> list;

}

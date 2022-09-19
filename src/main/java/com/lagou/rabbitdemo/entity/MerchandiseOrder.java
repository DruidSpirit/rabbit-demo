package com.lagou.rabbitdemo.entity;

import com.lagou.rabbitdemo.annotation.sqlcomment.Comment;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Comment("商品订单表")
@Table(name = "merchandise_order")
public class MerchandiseOrder {

    @Id
    @Comment("订单编号")
    @GeneratedValue(generator ="snowFlake" )
    @GenericGenerator(name="snowFlake", strategy="com.lagou.rabbitdemo.util.SnowIdGenerator")
    private Long id;

    @Comment("购买用户id")
    @Column(name = "purchase_id", nullable = false)
    private Long purchaseId;

    @Comment("商品编号")
    @Column(name = "Merchandise_id", nullable = false)
    private Long merchandiseId;

    @Comment("订单购买产品数量")
    @Column( name = "merchandise_count", nullable = false)
    private Integer merchandiseCount;

    @Comment("应付款金额")
    @Column( nullable = false)
    private BigDecimal paymentAmount;

    @Comment("付款状态(0-未支付，1-以支付，2-逾期未支付)")
    @Column( nullable = false, length = 1)
    private Integer paymentStatus;

    @Comment("下单时间")
    @Column( nullable = false)
    private LocalDateTime createTime;

}

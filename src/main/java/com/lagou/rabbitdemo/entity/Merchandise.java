package com.lagou.rabbitdemo.entity;

import com.lagou.rabbitdemo.annotation.sqlcomment.Comment;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@Entity
@Comment("商品表")
public class Merchandise {

    @Id
    @Comment("商品编号")
    @GeneratedValue(generator ="snowFlake" )
    @GenericGenerator(name="snowFlake", strategy="com.lagou.rabbitdemo.util.SnowIdGenerator")
    private Long id;

    @Comment("商品名称")
    @Column( nullable = false, length = 15)
    private String name;

    @Comment("商品价格")
    @Column(nullable = false)
    private BigDecimal price;
}

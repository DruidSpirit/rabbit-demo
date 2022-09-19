package com.lagou.rabbitdemo.entity;

import com.lagou.rabbitdemo.annotation.sqlcomment.Comment;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
@Comment("用户表")
public class User {

    @Id
    @Comment("用户id")
    @GeneratedValue(generator ="snowFlake" )
    @GenericGenerator(name="snowFlake", strategy="com.lagou.rabbitdemo.util.SnowIdGenerator")
    private Long userId;

    @Column(unique = true, nullable = false, length = 15)
    @Comment("用户名")
    private String username;

    @Column( nullable = false, length = 200)
    @Comment("登入密码")
    private String password;

}

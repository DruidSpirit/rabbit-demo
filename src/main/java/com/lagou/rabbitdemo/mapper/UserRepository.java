package com.lagou.rabbitdemo.mapper;

import com.lagou.rabbitdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findUserByUsername(String username );
}

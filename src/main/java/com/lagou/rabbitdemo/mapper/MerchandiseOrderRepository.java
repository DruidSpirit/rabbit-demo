package com.lagou.rabbitdemo.mapper;

import com.lagou.rabbitdemo.entity.MerchandiseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchandiseOrderRepository extends JpaRepository<MerchandiseOrder,Long> {
}

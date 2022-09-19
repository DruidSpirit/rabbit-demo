package com.lagou.rabbitdemo.mapper;

import com.lagou.rabbitdemo.entity.Merchandise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchandiseRepository extends JpaRepository<Merchandise,Long> {
}

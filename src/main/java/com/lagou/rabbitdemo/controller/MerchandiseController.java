package com.lagou.rabbitdemo.controller;

import com.lagou.rabbitdemo.dto.ResponseData;
import com.lagou.rabbitdemo.entity.Merchandise;
import com.lagou.rabbitdemo.mapper.MerchandiseRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("merchandise")
@AllArgsConstructor
public class MerchandiseController {

    private final MerchandiseRepository merchandiseRepository;

    /**
     * 获取商品列表
     */
    @RequestMapping("getMerchandiseList")
    public ResponseData<List<Merchandise>> getMerchandiseList (){

        List<Merchandise> all = merchandiseRepository.findAll();
       return ResponseData.SUCCESS(all);
    }

    /**
     * 根据商品id获取商品详情
     */
    @RequestMapping("getMerchandiseById/{id}")
    public ResponseData<Merchandise> getMerchandiseById(@PathVariable Long id){

        Merchandise merchandise = merchandiseRepository.findById(id).orElseGet(Merchandise::new);
        return ResponseData.SUCCESS(merchandise);
    }
}

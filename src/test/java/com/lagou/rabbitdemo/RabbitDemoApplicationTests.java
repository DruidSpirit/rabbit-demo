package com.lagou.rabbitdemo;

import com.lagou.rabbitdemo.entity.Merchandise;
import com.lagou.rabbitdemo.entity.MerchandiseOrder;
import com.lagou.rabbitdemo.entity.User;
import com.lagou.rabbitdemo.mapper.MerchandiseRepository;
import com.lagou.rabbitdemo.mapper.MerchandiseOrderRepository;
import com.lagou.rabbitdemo.mapper.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest
class RabbitDemoApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MerchandiseRepository merchandiseRepository;

    @Autowired
    private MerchandiseOrderRepository merchandiseOrderRepository;

    @Test
    void contextLoads() {

//        User user = new User();
//        user.setUsername("李四");
//        user.setPassword("12345");
//        User save1 = userRepository.save(user);

        for (int i = 0; i < 10; i++) {
            Merchandise merchandise = new Merchandise();
            merchandise.setName("商品"+i);
            merchandise.setPrice(BigDecimal.valueOf(10.9+i));
            Merchandise save = merchandiseRepository.save(merchandise);
        }


//        MerchandiseOrder merchandiseOrder = new MerchandiseOrder();
//        merchandiseOrder.setMerchandiseCount(2);
//        merchandiseOrder.setMerchandiseId(save.getId());
//        merchandiseOrder.setPaymentAmount(BigDecimal.valueOf(21.8));
//        merchandiseOrder.setPurchaseId(save1.getUserId());
//        merchandiseOrder.setCreateTime(LocalDateTime.now());
//
//        merchandiseOrderRepository.save(merchandiseOrder);
    }

    @Test
    public void testMd5(){


        String password = "37647365";
        String s = DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println(s);

        System.out.println(DigestUtils.md5DigestAsHex(password.getBytes()).equals(s));

    }

}

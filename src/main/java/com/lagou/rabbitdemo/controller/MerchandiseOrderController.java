package com.lagou.rabbitdemo.controller;

import com.lagou.rabbitdemo.dto.ResponseData;
import com.lagou.rabbitdemo.dto.ResponseDataEnums;
import com.lagou.rabbitdemo.dto.req.PayMoneyReq;
import com.lagou.rabbitdemo.dto.req.SubmitOrderReq;
import com.lagou.rabbitdemo.dto.res.OrderInfoRes;
import com.lagou.rabbitdemo.entity.Merchandise;
import com.lagou.rabbitdemo.entity.MerchandiseOrder;
import com.lagou.rabbitdemo.entity.User;
import com.lagou.rabbitdemo.enums.PaymentStatusEnum;
import com.lagou.rabbitdemo.mapper.MerchandiseOrderRepository;
import com.lagou.rabbitdemo.mapper.MerchandiseRepository;
import com.lagou.rabbitdemo.mapper.UserRepository;
import com.lagou.rabbitdemo.util.UtilForData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("merchandise")
@AllArgsConstructor
public class MerchandiseOrderController {

    private final MerchandiseOrderRepository merchandiseOrderRepository;
    private final MerchandiseRepository merchandiseRepository;
    private final UserRepository userRepository;
    private final AmqpTemplate amqpTemplate;


    /**
     * 提交订单
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @PostMapping("submitOrder")
    public ResponseData<?> submitOrder(HttpServletRequest httpServletRequest, @RequestBody SubmitOrderReq submitOrderReq) {

        //  获取登入用户
        Object sessionUser = httpServletRequest.getSession().getAttribute("user");
        if ( null == sessionUser ) {
            return ResponseData.FAILURE(ResponseDataEnums.RESPONSE_USER_NOT_LOGIN);
        }

        User user = (User)sessionUser;

        //  查询商品信息
        Merchandise merchandise = merchandiseRepository.findById(submitOrderReq.getMerchandiseId()).get();

        //  订单入库
        MerchandiseOrder merchandiseOrder = new MerchandiseOrder();
        merchandiseOrder.setMerchandiseId(submitOrderReq.getMerchandiseId());
        merchandiseOrder.setMerchandiseCount(submitOrderReq.getCount());
        merchandiseOrder.setPaymentAmount(
                merchandise.getPrice().multiply(BigDecimal.valueOf(submitOrderReq.getCount()))
        );
        merchandiseOrder.setCreateTime(LocalDateTime.now());
        merchandiseOrder.setPurchaseId(user.getUserId());
        merchandiseOrder.setPaymentStatus(PaymentStatusEnum.NO_PAY.getCode());
        MerchandiseOrder result = merchandiseOrderRepository.save(merchandiseOrder);

        //  将订单加入到mq延迟队列里面
        final MessageProperties messageProperties = MessagePropertiesBuilder.newInstance()
                // 设置消息的过期时间
                .setHeader("x-delay", 15 * 1000)
                .setContentEncoding("utf-8")
                .build();

        String sendOrderId = result.getId().toString();
        final Message message = MessageBuilder
                .withBody(sendOrderId.getBytes(StandardCharsets.UTF_8))
                .andProperties(messageProperties)
                .build();

        amqpTemplate.send("ex.delayed", "key.delayed", message);

        return result.getId() != null ? ResponseData.SUCCESS(result.getId()) : ResponseData.FAILURE("下单失败");
    }

    /**
     * 订单付款
     */
    @PostMapping("payMoney")
    public ResponseData<?> payMoney( @RequestBody PayMoneyReq payMoneyReq ){

        //  获取订单
        Optional<MerchandiseOrder> merchandiseOrder = merchandiseOrderRepository.findById(payMoneyReq.getOrderId());
        if (merchandiseOrder.isEmpty()) {
            return ResponseData.FAILURE(ResponseDataEnums.RESPONSE_FAIL_ORDER_NOT_EXIST);
        }

        // 根据订单状态处理不同业务
        PaymentStatusEnum enumByCode = UtilForData.getEnumByCode(PaymentStatusEnum.values(), PaymentStatusEnum::getCode, merchandiseOrder.get().getPaymentStatus());

        if ( null == enumByCode ) {
            return ResponseData.FAILURE(ResponseDataEnums.RESPONSE_SYSTEM_ERROR);
        }

        switch (enumByCode) {
            case HAS_PAY:
                return ResponseData.FAILURE(ResponseDataEnums.RESPONSE_FAIL_ORDER_HAS_PAY);
            case OVERDUE_NOT_PAY:
                return ResponseData.FAILURE(ResponseDataEnums.RESPONSE_FAIL_ORDER_OVERDUE_NOT_PAY);
            case NO_PAY:
                MerchandiseOrder order = merchandiseOrder.get();
                order.setPaymentStatus( PaymentStatusEnum.HAS_PAY.getCode() );
                merchandiseOrderRepository.save(order);
                return ResponseData.SUCCESS(ResponseDataEnums.RESPONSE_SUCCESS_ORDER_PAY);
            default:
                log.error("订单状态在代码中未处理{}",enumByCode.getCode());
                return ResponseData.FAILURE(ResponseDataEnums.RESPONSE_SYSTEM_ERROR);
        }

    }

    /**
     * 根据订单id获取订单详情
     */
    @GetMapping("/getOrderById/{orderId}")
    public ResponseData<?> getOrderById( @PathVariable Long orderId ){


        //  获取订单信息
        Optional<MerchandiseOrder> byId = merchandiseOrderRepository.findById(orderId);

        if (byId.isEmpty()) {
            return ResponseData.FAILURE(ResponseDataEnums.RESPONSE_FAIL_ORDER_NOT_EXIST);
        }

        //  组装前端相应信息
        MerchandiseOrder merchandiseOrder = byId.get();
        OrderInfoRes orderInfoRes = new OrderInfoRes();
        BeanUtils.copyProperties(merchandiseOrder,orderInfoRes);

        //  获取用户信息
        User user = userRepository.findById(merchandiseOrder.getPurchaseId()).orElseGet(User::new);
        orderInfoRes.setPurchaseName(user.getUsername());

        //  获取商品信息
        Merchandise merchandise = merchandiseRepository.findById(merchandiseOrder.getMerchandiseId()).orElseGet(Merchandise::new);
        orderInfoRes.setMerchandiseName(merchandise.getName());

        return ResponseData.SUCCESS(orderInfoRes);
    }
}

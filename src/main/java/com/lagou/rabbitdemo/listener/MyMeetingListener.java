package com.lagou.rabbitdemo.listener;

import com.lagou.rabbitdemo.entity.MerchandiseOrder;
import com.lagou.rabbitdemo.enums.PaymentStatusEnum;
import com.lagou.rabbitdemo.mapper.MerchandiseOrderRepository;
import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@AllArgsConstructor
public class MyMeetingListener {

    private final MerchandiseOrderRepository merchandiseOrderRepository;

    @RabbitListener(queues = "queue.delayed")
    public void onMessage(Message message, Channel channel) throws Exception {

        //  获取队列中的过期信息
        final String receive = new String(message.getBody(), message.getMessageProperties().getContentEncoding());
        final long orderId = Long.parseLong( receive );


        //  跟新数据库订单状态
        Optional<MerchandiseOrder> merchandiseOrderOptional = merchandiseOrderRepository.findById(orderId);
        if (merchandiseOrderOptional.isEmpty()) {
            throw new Exception("系统业务异常，队列中的订单在数据库中找不到");
        }

        MerchandiseOrder merchandiseOrder = merchandiseOrderOptional.get();

        if ( PaymentStatusEnum.HAS_PAY.getCode().equals(merchandiseOrder.getPaymentStatus())){
            // 消息确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }

        merchandiseOrder.setPaymentStatus(PaymentStatusEnum.OVERDUE_NOT_PAY.getCode());
        MerchandiseOrder save = merchandiseOrderRepository.save(merchandiseOrder);

        //  数据更新成功后给队列返回ack确认
        if ( PaymentStatusEnum.OVERDUE_NOT_PAY.getCode().equals(save.getPaymentStatus())) {
            // 消息确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }

    }


}

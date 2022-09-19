package com.lagou.rabbitdemo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
@EnableRabbit
public class RabbitConfig {

    @Bean
    public Queue queue() {
        return new Queue("queue.delayed", true, false, false, null);
    }

    @Bean
    public Exchange exchange() {

        Map<String, Object> arguments = new HashMap<>();
        // 使用x-delayed-type指定交换器的类型
        arguments.put("x-delayed-type", ExchangeTypes.DIRECT);
        // 使用x-delayed-message表示使用delayed exchange插件处理消息
        return new CustomExchange("ex.delayed", "x-delayed-message", true, false, arguments);
    }

    @Bean
    public Binding binding( Queue queue, Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("key.delayed").noargs();
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory factory) {
        return new RabbitAdmin(factory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory) {
        return new RabbitTemplate(factory);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory
                = new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        return factory;
    }

}
